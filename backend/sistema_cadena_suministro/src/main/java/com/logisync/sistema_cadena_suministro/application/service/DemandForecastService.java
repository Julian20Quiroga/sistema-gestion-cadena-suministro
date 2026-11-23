package com.logisync.sistema_cadena_suministro.application.service;

import com.logisync.sistema_cadena_suministro.application.commands.GenerateForecastCommand;
import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.ports.driven.GenerateForecastUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.InventoryRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.OrderRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.ProductRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.SupplyNeedRepository;
import com.logisync.sistema_cadena_suministro.application.responses.ForecastBatchSummaryAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.ProductForecastAppResponse;
import com.logisync.sistema_cadena_suministro.domain.enums.SupplyNeedOrigin;
import com.logisync.sistema_cadena_suministro.domain.enums.SupplyNeedStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.forecast.decorator.SafetyStockDecorator;
import com.logisync.sistema_cadena_suministro.domain.forecast.decorator.SeasonalAdjustmentDecorator;
import com.logisync.sistema_cadena_suministro.domain.forecast.interpreter.SupplyNeedRuleContext;
import com.logisync.sistema_cadena_suministro.domain.forecast.interpreter.SupplyNeedRuleInterpreter;
import com.logisync.sistema_cadena_suministro.domain.forecast.strategy.ForecastStrategy;
import com.logisync.sistema_cadena_suministro.domain.forecast.strategy.ForecastStrategyFactory;
import com.logisync.sistema_cadena_suministro.domain.inventory.StockTotals;
import com.logisync.sistema_cadena_suministro.domain.models.ForecastResult;
import com.logisync.sistema_cadena_suministro.domain.models.HistoricalDataPoint;
import com.logisync.sistema_cadena_suministro.domain.models.Product;
import com.logisync.sistema_cadena_suministro.domain.models.SupplyNeed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de aplicación para la proyección predictiva de demanda de inventario.
 * Utiliza Strategy y Decorator para la predicción, e Interpreter para evaluar reglas de abastecimiento.
 */
@Service
@RequiredArgsConstructor
public class DemandForecastService implements GenerateForecastUseCase {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;
    private final SupplyNeedRepository supplyNeedRepository;
    private final SupplyNeedRuleInterpreter supplyNeedRuleInterpreter = new SupplyNeedRuleInterpreter();

    /**
     * Genera proyecciones de demanda en lote para un producto específico o todo el catálogo.
     * Patrón aplicado: Strategy (ejecuta el algoritmo de predicción seleccionado) y
     * Decorator (añade colchón de seguridad y multiplicador estacional dinámicamente).
     * Si se detecta déficit frente al stock actual, genera automáticamente una necesidad de abastecimiento.
     */
    @Override
    @Transactional
    public ForecastBatchSummaryAppResponse generate(GenerateForecastCommand command) {
        List<Product> products = new ArrayList<>();
        if (command.productId() != null) {
            Product p = productRepository.findById(command.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", command.productId()));
            products.add(p);
        } else {
            products.addAll(productRepository.findAll(null, PageQuery.of(0, PageQuery.MAX_SIZE)).items());
        }

        ForecastStrategy strategy = buildConfiguredStrategy(
                command.algorithm(),
                command.seasonalMultiplier(),
                command.safetyStockBufferPercentage()
        );

        List<ProductForecastAppResponse> results = new ArrayList<>();
        int supplyNeedsCreated = 0;

        for (Product product : products) {
            List<HistoricalDataPoint> history = buildHistoricalDemand(product.getId());
            ForecastResult forecast = strategy.forecast(history, command.periodsAhead());

            StockTotals totals = inventoryRepository.totalsByProduct(product.getId());
            int availableStock = (int) totals.available();
            int required = (int) Math.ceil(forecast.recommendedProcurementQuantity());
            int shortage = Math.max(0, required - availableStock);

            boolean needCreated = false;
            Integer needId = null;

            boolean openNeedExists = command.autoCreateSupplyNeeds()
                    && shortage > 0
                    && supplyNeedRepository.existsOpenByProduct(product.getId());
            SupplyNeedRuleContext ruleContext = new SupplyNeedRuleContext(
                    command.autoCreateSupplyNeeds(),
                    shortage,
                    openNeedExists);

            if (supplyNeedRuleInterpreter.shouldCreate(ruleContext)) {
                SupplyNeed need = new SupplyNeed(
                        null,
                        product.getId(),
                        shortage,
                        SupplyNeedOrigin.FORECAST,
                        SupplyNeedStatus.OPEN,
                        null,
                        LocalDateTime.now(),
                        null
                );
                SupplyNeed savedNeed = supplyNeedRepository.save(need);
                needCreated = true;
                needId = savedNeed.getId();
                supplyNeedsCreated++;
            }

            List<Double> histValues = history.stream().map(HistoricalDataPoint::quantity).toList();
            results.add(new ProductForecastAppResponse(
                    product.getId(),
                    product.getName(),
                    product.getSku(),
                    forecast.algorithmName(),
                    histValues,
                    forecast.projectedQuantities(),
                    forecast.recommendedProcurementQuantity(),
                    availableStock,
                    shortage,
                    needCreated,
                    needId
            ));
        }

        return new ForecastBatchSummaryAppResponse(products.size(), supplyNeedsCreated, results);
    }

    /**
     * Consulta la previsión de demanda individual para un único producto y período especificado.
     * Patrón aplicado: Strategy (ejecuta el algoritmo estadístico solicitado).
     */
    @Override
    @Transactional(readOnly = true)
    public ProductForecastAppResponse getForecastForProduct(Integer productId, String algorithm, int periodsAhead) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        ForecastStrategy strategy = buildConfiguredStrategy(algorithm, null, null);
        List<HistoricalDataPoint> history = buildHistoricalDemand(productId);
        ForecastResult forecast = strategy.forecast(history, periodsAhead);
        StockTotals totals = inventoryRepository.totalsByProduct(productId);
        int available = (int) totals.available();
        int required = (int) Math.ceil(forecast.recommendedProcurementQuantity());
        int shortage = Math.max(0, required - available);

        return new ProductForecastAppResponse(
                product.getId(),
                product.getName(),
                product.getSku(),
                forecast.algorithmName(),
                history.stream().map(HistoricalDataPoint::quantity).toList(),
                forecast.projectedQuantities(),
                forecast.recommendedProcurementQuantity(),
                available,
                shortage,
                false,
                null
        );
    }

    /**
     * Ensambla en tiempo de ejecución la estrategia base y la envuelve con decoradores si aplica.
     * Patrones aplicados: Factory Method (instancia la estrategia) y Decorator (envuelve con ajustes).
     */
    private ForecastStrategy buildConfiguredStrategy(String algorithm, Double seasonal, Double safetyBuffer) {
        ForecastStrategy base = ForecastStrategyFactory.create(algorithm);
        ForecastStrategy current = base;

        if (seasonal != null && seasonal > 0.0 && Math.abs(seasonal - 1.0) > 0.001) {
            current = new SeasonalAdjustmentDecorator(current, seasonal);
        }
        if (safetyBuffer != null && safetyBuffer > 0.0) {
            current = new SafetyStockDecorator(current, safetyBuffer);
        }
        return current;
    }

    /**
     * Construye la serie histórica de demanda de referencia para la estabilidad matemática del modelo.
     */
    private List<HistoricalDataPoint> buildHistoricalDemand(Integer productId) {
        YearMonth currentMonth = YearMonth.now(ZoneId.of("America/Bogota"));
        YearMonth firstMonth = currentMonth.minusMonths(11);
        LocalDateTime from = firstMonth.atDay(1).atStartOfDay();
        LocalDateTime to = currentMonth.plusMonths(1).atDay(1).atStartOfDay();
        Map<YearMonth, Double> quantitiesByMonth = new HashMap<>();
        orderRepository.findDeliveredDemand(from, to, productId).forEach(record -> {
            YearMonth month = YearMonth.from(record.deliveredOrderDate());
            quantitiesByMonth.merge(month, record.quantity().doubleValue(), Double::sum);
        });
        List<HistoricalDataPoint> history = new ArrayList<>(12);
        for (int index = 0; index < 12; index++) {
            YearMonth month = firstMonth.plusMonths(index);
            history.add(new HistoricalDataPoint(index + 1, quantitiesByMonth.getOrDefault(month, 0.0)));
        }
        return history;
    }
}

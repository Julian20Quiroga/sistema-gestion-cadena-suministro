package com.logisync.sistema_cadena_suministro.infrastructure.controllers;

import com.logisync.sistema_cadena_suministro.application.commands.GenerateForecastCommand;
import com.logisync.sistema_cadena_suministro.application.ports.driven.GenerateForecastUseCase;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.GenerateForecastRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.ForecastBatchSummaryResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.ProductForecastResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers.ForecastRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/forecast")
@RequiredArgsConstructor
public class DemandForecastController {

    private final GenerateForecastUseCase generateForecastUseCase;
    private final ForecastRestMapper mapper;

    /**
     * Genera la proyección de demanda para el catálogo o un producto específico y genera necesidades automáticas.
     * Patrones aplicados: Strategy (ejecuta algoritmo predictivo) y Decorator (agrega colchón y factores estacionales).
     */
    @PostMapping("/generate")
    public ResponseEntity<ForecastBatchSummaryResponse> generate(@Valid @RequestBody(required = false) GenerateForecastRequest request) {
        GenerateForecastRequest req = (request != null) ? request : new GenerateForecastRequest(null, "MOVING_AVERAGE", 3, null, null, true);
        var appResp = generateForecastUseCase.generate(mapper.toCommand(req));
        return ResponseEntity.ok(mapper.toResponse(appResp));
    }

    /**
     * Consulta la predicción puntual de demanda para un producto individual y número de periodos especificado.
     * Patrón aplicado: Strategy (ejecuta el algoritmo estadístico seleccionado).
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductForecastResponse> getProductForecast(
            @PathVariable("id") Integer productId,
            @RequestParam(value = "algorithm", defaultValue = "MOVING_AVERAGE") String algorithm,
            @RequestParam(value = "periodsAhead", defaultValue = "3") int periodsAhead) {
        var appResp = generateForecastUseCase.getForecastForProduct(productId, algorithm, periodsAhead);
        return ResponseEntity.ok(mapper.toResponse(appResp));
    }

    /**
     * Lista los algoritmos disponibles (estrategias) y los decoradores de ajuste matemático configurados.
     */
    @GetMapping("/strategies")
    public ResponseEntity<Map<String, Object>> getStrategies() {
        return ResponseEntity.ok(Map.of(
                "strategies", List.of("MOVING_AVERAGE", "EXPONENTIAL_SMOOTHING", "LINEAR_REGRESSION"),
                "decorators", List.of("SeasonalAdjustmentDecorator", "SafetyStockDecorator"),
                "patternDesign", "GoF Strategy & GoF Decorator"
        ));
    }
}

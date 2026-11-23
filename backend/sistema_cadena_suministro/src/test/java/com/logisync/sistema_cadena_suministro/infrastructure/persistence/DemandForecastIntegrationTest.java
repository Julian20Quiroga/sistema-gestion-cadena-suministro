package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.application.commands.CreateProductCommand;
import com.logisync.sistema_cadena_suministro.application.commands.GenerateForecastCommand;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreateProductUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.GenerateForecastUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.SupplyNeedRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.OrderRepository;
import com.logisync.sistema_cadena_suministro.application.responses.ForecastBatchSummaryAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.ProductAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.ProductForecastAppResponse;
import com.logisync.sistema_cadena_suministro.domain.enums.SupplyNeedOrigin;
import com.logisync.sistema_cadena_suministro.domain.models.SupplyNeed;
import com.logisync.sistema_cadena_suministro.domain.models.Order;
import com.logisync.sistema_cadena_suministro.domain.models.OrderDetail;
import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class DemandForecastIntegrationTest {

    @Autowired
    private com.logisync.sistema_cadena_suministro.application.ports.driving.ProductRepository productRepository;

    @Autowired
    private com.logisync.sistema_cadena_suministro.application.ports.driving.SupplierRepository supplierRepository;

    @Autowired
    private GenerateForecastUseCase generateForecastUseCase;

    @Autowired
    private SupplyNeedRepository supplyNeedRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void generateForecastCreatesSupplyNeedWhenStockIsShort() {
        var supplier = supplierRepository.create(new com.logisync.sistema_cadena_suministro.domain.models.Supplier(
                null, "NIT-" + UUID.randomUUID().toString().substring(0, 5), "Supplier FC", "3000", "fc@sup.co", null
        ));

        String sku = "FC-" + UUID.randomUUID().toString().substring(0, 8);
        var product = new com.logisync.sistema_cadena_suministro.domain.models.Product();
        product.setName("Forecasted Product");
        product.setSupplierId(supplier.getId());
        product.setSku(sku);
        product.setSalePrice(new BigDecimal("150.00"));
        product.setReorderPoint(10);
        var savedProduct = productRepository.create(product);

        Order deliveredOrder = new Order();
        deliveredOrder.setOrderReference("FC-ORDER-" + UUID.randomUUID().toString().substring(0, 8));
        deliveredOrder.setDeliveryAddress("Test address");
        deliveredOrder.setDeliveryCity("Bucaramanga");
        deliveredOrder.setDeliveryCountry("Colombia");
        deliveredOrder.setCustomerPhone("3000000000");
        deliveredOrder.setCustomerName("Forecast test");
        deliveredOrder.setStatus(OrderStatus.ENTREGADO);
        deliveredOrder.setDetails(List.of(new OrderDetail(null, savedProduct.getId(), null, 100, new BigDecimal("150.00"))));
        Order savedDeliveredOrder = orderRepository.save(deliveredOrder);
        assertNotNull(savedDeliveredOrder.getCreatedAt());

        // Available stock is 0, so forecast will have a shortage and create SupplyNeed with origin FORECAST
        GenerateForecastCommand cmd = new GenerateForecastCommand(
                savedProduct.getId(),
                "LINEAR_REGRESSION",
                3,
                1.15, // Seasonal decorator
                0.10, // Safety stock decorator
                true
        );

        ForecastBatchSummaryAppResponse summary = generateForecastUseCase.generate(cmd);
        assertEquals(1, summary.productsProcessed());
        assertEquals(1, summary.supplyNeedsCreated());

        ProductForecastAppResponse forecast = summary.forecasts().get(0);
        assertTrue(forecast.supplyNeedCreated());
        assertNotNull(forecast.supplyNeedId());
        assertEquals(12, forecast.historicalPoints().size());
        assertEquals(100.0, forecast.historicalPoints().get(11));

        SupplyNeed need = supplyNeedRepository.findById(forecast.supplyNeedId()).orElseThrow();
        assertEquals(SupplyNeedOrigin.FORECAST, need.getOrigin());
        assertEquals(savedProduct.getId(), need.getProductId());
        assertTrue(need.getRequestedQuantity() > 0);
    }

    @Test
    void generateForecastForCatalogIncludesFirstPageProductsAndTheirHistory() {
        var supplier = supplierRepository.create(new com.logisync.sistema_cadena_suministro.domain.models.Supplier(
                null, "NIT-" + UUID.randomUUID().toString().substring(0, 5), "Catalog Supplier", "3000", "catalog@sup.co", null
        ));
        var product = new com.logisync.sistema_cadena_suministro.domain.models.Product();
        product.setName("Catalog Forecast Product");
        product.setSupplierId(supplier.getId());
        product.setSku("FC-CATALOG-" + UUID.randomUUID().toString().substring(0, 8));
        product.setSalePrice(new BigDecimal("150.00"));
        product.setReorderPoint(10);
        var savedProduct = productRepository.create(product);

        Order deliveredOrder = new Order();
        deliveredOrder.setOrderReference("FC-CATALOG-ORDER-" + UUID.randomUUID().toString().substring(0, 8));
        deliveredOrder.setDeliveryAddress("Test address");
        deliveredOrder.setDeliveryCity("Bucaramanga");
        deliveredOrder.setDeliveryCountry("Colombia");
        deliveredOrder.setCustomerPhone("3000000000");
        deliveredOrder.setCustomerName("Catalog forecast test");
        deliveredOrder.setStatus(OrderStatus.ENTREGADO);
        deliveredOrder.setDetails(List.of(new OrderDetail(null, savedProduct.getId(), null, 100, new BigDecimal("150.00"))));
        Order savedDeliveredOrder = orderRepository.save(deliveredOrder);
        assertNotNull(savedDeliveredOrder.getCreatedAt());

        ForecastBatchSummaryAppResponse summary = generateForecastUseCase.generate(new GenerateForecastCommand(
                null, "MOVING_AVERAGE", 3, null, null, false));

        ProductForecastAppResponse forecast = summary.forecasts().stream()
                .filter(item -> item.productId().equals(savedProduct.getId()))
                .findFirst().orElseThrow();
        assertEquals(12, forecast.historicalPoints().size());
        assertEquals(100.0, forecast.historicalPoints().get(11));
        assertTrue(forecast.totalProjectedDemand() > 0.0);
    }
}

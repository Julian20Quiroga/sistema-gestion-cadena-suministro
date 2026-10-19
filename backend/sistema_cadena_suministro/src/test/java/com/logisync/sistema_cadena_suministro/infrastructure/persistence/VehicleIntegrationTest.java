package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.application.commands.CreateOrderCommand;
import com.logisync.sistema_cadena_suministro.application.commands.CreateVehicleCommand;
import com.logisync.sistema_cadena_suministro.application.commands.OptimizeRouteCommand;
import com.logisync.sistema_cadena_suministro.application.commands.RegisterEntryCommand;
import com.logisync.sistema_cadena_suministro.application.ports.driven.RegisterEntryUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.ProductRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.StorageSpaceRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.SupplierRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.VehicleRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.WarehouseRepository;
import com.logisync.sistema_cadena_suministro.application.responses.OptimizedRouteAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.OrderAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.VehicleAppResponse;
import com.logisync.sistema_cadena_suministro.application.service.OrderService;
import com.logisync.sistema_cadena_suministro.application.service.VehicleService;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;
import com.logisync.sistema_cadena_suministro.domain.models.Product;
import com.logisync.sistema_cadena_suministro.domain.models.StorageSpace;
import com.logisync.sistema_cadena_suministro.domain.models.Supplier;
import com.logisync.sistema_cadena_suministro.domain.models.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class VehicleIntegrationTest {

    private static final AtomicInteger SEQ = new AtomicInteger();

    @Autowired
    VehicleService vehicleService;
    @Autowired
    VehicleRepository vehicleRepository;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    StorageSpaceRepository storageSpaceRepository;
    @Autowired
    SupplierRepository supplierRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderService orderService;
    @Autowired
    RegisterEntryUseCase registerEntryUseCase;

    Integer warehouseId;
    Integer productId;

    @BeforeEach
    void setUp() {
        int n = SEQ.incrementAndGet();
        Warehouse wh = new Warehouse(null, "Route-WH-" + n, "Route St", "300", SoftDeleteStatus.ACTIVE, 1,
                new BigDecimal("4.609710"), new BigDecimal("-74.081750"));
        warehouseId = warehouseRepository.create(wh).getId();

        StorageSpace zone = storageSpaceRepository.create(StorageSpace.create("RZ-" + n, warehouseId, StorageSpaceType.ZONE, null, null));
        StorageSpace rack = storageSpaceRepository.create(StorageSpace.create("RR-" + n, warehouseId, StorageSpaceType.RACK, null, zone));
        StorageSpace loc = storageSpaceRepository.create(StorageSpace.create("RL-" + n, warehouseId, StorageSpaceType.LOCATION, 500, rack));

        Supplier sup = supplierRepository.create(new Supplier(null, "R-NIT-" + n, "Route Supplier " + n, "300", "r" + n + "@s.co", null));
        Product prod = new Product();
        prod.setName("Route Product " + n);
        prod.setSupplierId(sup.getId());
        prod.setSku("R-SKU-" + n);
        prod.setSalePrice(new BigDecimal("10.00"));
        prod.setWeightKg(new BigDecimal("1.5"));
        prod.setReorderPoint(0);
        productId = productRepository.create(prod).getId();

        registerEntryUseCase.registerEntry(new RegisterEntryCommand(loc.getId(), productId, 200, "INIT-ROUTE-STOCK"));
    }

    @Test
    void createsVehicleWithApiKeyAndOptimizesRouteForOrders() {
        int n = SEQ.incrementAndGet();
        CreateVehicleCommand cmd = new CreateVehicleCommand("PLT-" + n, "Truck 2026", new BigDecimal("5000.00"), new BigDecimal("45.00"));
        VehicleAppResponse created = vehicleService.create(cmd);

        assertNotNull(created.rawApiKey());
        assertTrue(created.rawApiKey().startsWith("VK-"));
        assertEquals("PLT-" + n, created.plate());

        // Rotate key
        VehicleAppResponse rotated = vehicleService.rotateApiKey(created.id());
        assertNotEquals(created.rawApiKey(), rotated.rawApiKey());

        // Create 2 orders with coordinates
        CreateOrderCommand o1 = new CreateOrderCommand(
                "ORD-R1-" + n, "Address 1", "Bogota", "Colombia", "3001", "Client A",
                new BigDecimal("4.620000"), new BigDecimal("-74.070000"),
                List.of(new CreateOrderCommand.Item(productId, 5, new BigDecimal("10.00")))
        );
        OrderAppResponse ord1 = orderService.create(o1);

        CreateOrderCommand o2 = new CreateOrderCommand(
                "ORD-R2-" + n, "Address 2", "Bogota", "Colombia", "3002", "Client B",
                new BigDecimal("4.650000"), new BigDecimal("-74.050000"),
                List.of(new CreateOrderCommand.Item(productId, 10, new BigDecimal("10.00")))
        );
        OrderAppResponse ord2 = orderService.create(o2);

        // Optimize route
        OptimizeRouteCommand optCmd = new OptimizeRouteCommand(
                warehouseId,
                created.id(),
                List.of(ord1.id(), ord2.id()),
                "TWO_OPT"
        );
        OptimizedRouteAppResponse route = vehicleService.optimizeRoute(optCmd);

        assertEquals("TWO_OPT", route.strategyUsed());
        assertEquals(3, route.stops().size()); // Warehouse + 2 orders
        assertTrue(route.totalDistanceKm() > 0);
        assertEquals(new BigDecimal("22.50"), route.totalPayloadKg()); // (5 + 10) * 1.5 kg
    }
}

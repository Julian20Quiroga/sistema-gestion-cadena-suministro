package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.application.commands.CreateOrderCommand;
import com.logisync.sistema_cadena_suministro.application.commands.RegisterEntryCommand;
import com.logisync.sistema_cadena_suministro.application.ports.driven.RegisterEntryUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.OrderRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.ProductRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.StorageSpaceRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.SupplierRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.WarehouseRepository;
import com.logisync.sistema_cadena_suministro.application.responses.OrderAppResponse;
import com.logisync.sistema_cadena_suministro.application.service.OrderService;
import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;
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

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OrderIntegrationTest {

    private static final AtomicInteger SEQ = new AtomicInteger();

    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    SupplierRepository supplierRepository;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    StorageSpaceRepository storageSpaceRepository;
    @Autowired
    RegisterEntryUseCase registerEntryUseCase;

    Integer productId;

    @BeforeEach
    void setUp() {
        int n = SEQ.incrementAndGet();
        Supplier supplier = supplierRepository.create(new Supplier(null, "ORD-NIT-" + n, "Supplier " + n, "300", "o" + n + "@s.co", null));
        Product product = new Product();
        product.setName("Order Product " + n);
        product.setSupplierId(supplier.getId());
        product.setSku("ORD-SKU-" + n);
        product.setSalePrice(new BigDecimal("25.00"));
        product.setWeightKg(new BigDecimal("2.0"));
        product.setReorderPoint(0);
        productId = productRepository.create(product).getId();

        Warehouse wh = warehouseRepository.create(new Warehouse(null, "ORD-WH-" + n, "Street", "300", SoftDeleteStatus.ACTIVE, 1, null, null));
        StorageSpace zone = storageSpaceRepository.create(StorageSpace.create("ORD-Z-" + n, wh.getId(), StorageSpaceType.ZONE, null, null));
        StorageSpace rack = storageSpaceRepository.create(StorageSpace.create("ORD-R-" + n, wh.getId(), StorageSpaceType.RACK, null, zone));
        StorageSpace loc = storageSpaceRepository.create(StorageSpace.create("ORD-LOC-" + n, wh.getId(), StorageSpaceType.LOCATION, 100, rack));

        registerEntryUseCase.registerEntry(new RegisterEntryCommand(loc.getId(), productId, 50, "INIT-STOCK"));
    }

    @Test
    void createsOrderAndTransitionsThroughLifecycle() {
        CreateOrderCommand cmd = new CreateOrderCommand(
                "ORD-REF-" + SEQ.incrementAndGet(),
                "Main Ave 123",
                "Bogota",
                "Colombia",
                "3001234567",
                "Alice",
                new BigDecimal("4.609710"),
                new BigDecimal("-74.081750"),
                List.of(new CreateOrderCommand.Item(productId, 10, new BigDecimal("25.00")))
        );

        OrderAppResponse created = orderService.create(cmd);
        assertEquals(OrderStatus.REGISTRADO, created.status());
        assertEquals(new BigDecimal("250.00"), created.totalAmount());

        OrderAppResponse prep = orderService.startPreparation(created.id());
        assertEquals(OrderStatus.PREPARACION, prep.status());

        OrderAppResponse dispatched = orderService.markDispatched(created.id());
        assertEquals(OrderStatus.DESPACHADO, dispatched.status());

        OrderAppResponse delivered = orderService.markDelivered(created.id());
        assertEquals(OrderStatus.ENTREGADO, delivered.status());
    }
}

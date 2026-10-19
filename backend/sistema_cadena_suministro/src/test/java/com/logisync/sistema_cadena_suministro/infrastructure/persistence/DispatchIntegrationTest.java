package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.application.commands.CreateDispatchCommand;
import com.logisync.sistema_cadena_suministro.application.commands.CreateOrderCommand;
import com.logisync.sistema_cadena_suministro.application.commands.CreateVehicleCommand;
import com.logisync.sistema_cadena_suministro.application.commands.RegisterEntryCommand;
import com.logisync.sistema_cadena_suministro.application.ports.driven.RegisterEntryUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.InventoryRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.OrderRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.ProductRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.StorageSpaceRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.SupplierRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.WarehouseRepository;
import com.logisync.sistema_cadena_suministro.application.responses.DispatchAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.OrderAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.VehicleAppResponse;
import com.logisync.sistema_cadena_suministro.application.service.DispatchService;
import com.logisync.sistema_cadena_suministro.application.service.OrderService;
import com.logisync.sistema_cadena_suministro.application.service.VehicleService;
import com.logisync.sistema_cadena_suministro.domain.enums.DispatchStatus;
import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;
import com.logisync.sistema_cadena_suministro.domain.inventory.StockTotals;
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
class DispatchIntegrationTest {

    private static final AtomicInteger SEQ = new AtomicInteger();

    @Autowired
    DispatchService dispatchService;
    @Autowired
    OrderService orderService;
    @Autowired
    VehicleService vehicleService;
    @Autowired
    SupplierRepository supplierRepository;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    StorageSpaceRepository storageSpaceRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    InventoryRepository inventoryRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    RegisterEntryUseCase registerEntryUseCase;

    Integer warehouseId;
    Integer productId;
    Integer vehicleId;

    @BeforeEach
    void setUp() {
        int n = SEQ.incrementAndGet();
        Warehouse wh = new Warehouse(null, "Disp-WH-" + n, "Street", "300", SoftDeleteStatus.ACTIVE, 1, null, null);
        warehouseId = warehouseRepository.create(wh).getId();

        StorageSpace zone = storageSpaceRepository.create(StorageSpace.create("DZ-" + n, warehouseId, StorageSpaceType.ZONE, null, null));
        StorageSpace rack = storageSpaceRepository.create(StorageSpace.create("DR-" + n, warehouseId, StorageSpaceType.RACK, null, zone));
        StorageSpace loc = storageSpaceRepository.create(StorageSpace.create("DL-" + n, warehouseId, StorageSpaceType.LOCATION, 500, rack));

        Supplier sup = supplierRepository.create(new Supplier(null, "D-NIT-" + n, "Disp Sup " + n, "300", "d" + n + "@s.co", null));
        Product prod = new Product();
        prod.setName("Disp Product " + n);
        prod.setSupplierId(sup.getId());
        prod.setSku("D-SKU-" + n);
        prod.setSalePrice(new BigDecimal("50.00"));
        prod.setWeightKg(new BigDecimal("1.0"));
        prod.setReorderPoint(0);
        productId = productRepository.create(prod).getId();

        registerEntryUseCase.registerEntry(new RegisterEntryCommand(loc.getId(), productId, 100, "INIT-DISP-STOCK"));

        VehicleAppResponse vRes = vehicleService.create(new CreateVehicleCommand("DSP-" + n, "Truck", new BigDecimal("2000.00"), new BigDecimal("40.00")));
        vehicleId = vRes.id();
    }

    @Test
    void createDispatchReservesStockAndShippingConsumesIt() {
        int n = SEQ.incrementAndGet();
        CreateOrderCommand oCmd = new CreateOrderCommand(
                "ORD-DSP-" + n, "Customer St", "Medellin", "Colombia", "300", "Customer",
                null, null, List.of(new CreateOrderCommand.Item(productId, 15, new BigDecimal("50.00")))
        );
        OrderAppResponse ord = orderService.create(oCmd);

        // 1. Create dispatch -> reserves 15 units
        CreateDispatchCommand dCmd = new CreateDispatchCommand(ord.id(), warehouseId, vehicleId);
        DispatchAppResponse dispatch = dispatchService.create(dCmd);

        assertEquals(DispatchStatus.PENDIENTE, dispatch.status());
        assertEquals(1, dispatch.details().size());
        assertEquals(15, dispatch.details().get(0).quantity());

        StockTotals totalsReserved = inventoryRepository.totalsByProduct(productId);
        assertEquals(100, totalsReserved.physical());
        assertEquals(15, totalsReserved.reserved());
        assertEquals(85, totalsReserved.available());

        // 2. Start preparation
        dispatch = dispatchService.startPreparation(dispatch.id());
        assertEquals(DispatchStatus.PREPARANDO, dispatch.status());

        // 3. Ship -> consumes reserved stock
        dispatch = dispatchService.ship(dispatch.id());
        assertEquals(DispatchStatus.EN_TRANSPORTE, dispatch.status());

        StockTotals totalsShipped = inventoryRepository.totalsByProduct(productId);
        assertEquals(85, totalsShipped.physical());
        assertEquals(0, totalsShipped.reserved());
        assertEquals(85, totalsShipped.available());

        assertEquals(OrderStatus.DESPACHADO, orderRepository.findById(ord.id()).orElseThrow().getStatus());

        // 4. Deliver
        dispatch = dispatchService.deliver(dispatch.id());
        assertEquals(DispatchStatus.ENTREGADO, dispatch.status());
        assertEquals(OrderStatus.ENTREGADO, orderRepository.findById(ord.id()).orElseThrow().getStatus());
    }

    @Test
    void cancellingDispatchReleasesReservedStock() {
        int n = SEQ.incrementAndGet();
        CreateOrderCommand oCmd = new CreateOrderCommand(
                "ORD-CAN-" + n, "Customer St", "Medellin", "Colombia", "300", "Customer",
                null, null, List.of(new CreateOrderCommand.Item(productId, 20, new BigDecimal("50.00")))
        );
        OrderAppResponse ord = orderService.create(oCmd);

        CreateDispatchCommand dCmd = new CreateDispatchCommand(ord.id(), warehouseId, vehicleId);
        DispatchAppResponse dispatch = dispatchService.create(dCmd);

        assertEquals(20, inventoryRepository.totalsByProduct(productId).reserved());

        // Cancel
        dispatch = dispatchService.cancel(dispatch.id());
        assertEquals(DispatchStatus.CANCELADO, dispatch.status());

        StockTotals totals = inventoryRepository.totalsByProduct(productId);
        assertEquals(100, totals.physical());
        assertEquals(0, totals.reserved()); // Released!
        assertEquals(100, totals.available());

        assertEquals(OrderStatus.ANULADO, orderRepository.findById(ord.id()).orElseThrow().getStatus());
    }
}

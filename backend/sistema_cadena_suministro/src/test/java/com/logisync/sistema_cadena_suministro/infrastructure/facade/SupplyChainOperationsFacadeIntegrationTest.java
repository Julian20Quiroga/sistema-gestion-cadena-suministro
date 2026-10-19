package com.logisync.sistema_cadena_suministro.infrastructure.facade;

import com.logisync.sistema_cadena_suministro.application.commands.CreateOrderCommand;
import com.logisync.sistema_cadena_suministro.application.commands.CreatePurchaseOrderCommand;
import com.logisync.sistema_cadena_suministro.application.commands.RegisterEntryCommand;
import com.logisync.sistema_cadena_suministro.application.facade.SupplyChainOperationsFacade;
import com.logisync.sistema_cadena_suministro.application.ports.driven.RegisterEntryUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.ProductRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.StorageSpaceRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.SupplierRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.WarehouseRepository;
import com.logisync.sistema_cadena_suministro.application.responses.OrderAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.PurchaseOrderAppResponse;
import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;
import com.logisync.sistema_cadena_suministro.domain.enums.PurchaseOrderStatus;
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
class SupplyChainOperationsFacadeIntegrationTest {

    private static final AtomicInteger SEQ = new AtomicInteger();

    @Autowired
    SupplyChainOperationsFacade facade;
    @Autowired
    SupplierRepository supplierRepository;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    StorageSpaceRepository storageSpaceRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    RegisterEntryUseCase registerEntryUseCase;

    Integer supplierId;
    Integer warehouseId;
    Integer locationId;
    Integer productId;

    @BeforeEach
    void setUp() {
        int n = SEQ.incrementAndGet();
        Supplier supplier = supplierRepository.create(new Supplier(null, "FAC-NIT-" + n, "Supplier Facade " + n, "300", "fac" + n + "@s.co", null));
        supplierId = supplier.getId();

        Warehouse wh = warehouseRepository.create(new Warehouse(null, "FAC-WH-" + n, "Street", "300", SoftDeleteStatus.ACTIVE, 1, null, null));
        warehouseId = wh.getId();

        StorageSpace zone = storageSpaceRepository.create(StorageSpace.create("FAC-Z-" + n, warehouseId, StorageSpaceType.ZONE, null, null));
        StorageSpace rack = storageSpaceRepository.create(StorageSpace.create("FAC-R-" + n, warehouseId, StorageSpaceType.RACK, null, zone));
        locationId = storageSpaceRepository.create(StorageSpace.create("FAC-L-" + n, warehouseId, StorageSpaceType.LOCATION, 500, rack)).getId();

        Product product = new Product();
        product.setName("Facade Product " + n);
        product.setSupplierId(supplierId);
        product.setSku("FAC-SKU-" + n);
        product.setSalePrice(new BigDecimal("30.00"));
        product.setWeightKg(new BigDecimal("1.2"));
        product.setReorderPoint(0);
        productId = productRepository.create(product).getId();
    }

    @Test
    void facadeOrchestratesProcurementAndOrdersSimultaneously() {
        // 1. Procure and receive directly via Facade
        CreatePurchaseOrderCommand poCmd = new CreatePurchaseOrderCommand(
                supplierId,
                warehouseId,
                List.of(new CreatePurchaseOrderCommand.DetailItem(productId, 30, new BigDecimal("20.00")))
        );
        PurchaseOrderAppResponse poRes = facade.procureAndReceiveMerchandise(poCmd, locationId);
        assertEquals(PurchaseOrderStatus.RECIBIDA, poRes.status());

        // 2. Check consolidated stock via Facade
        StockTotals totals = facade.getConsolidatedStock(productId);
        assertEquals(30, totals.physical());

        // 3. Place customer order via Facade
        CreateOrderCommand orderCmd = new CreateOrderCommand(
                "FAC-ORD-" + SEQ.incrementAndGet(),
                "Customer Ave 45",
                "Cali",
                "Colombia",
                "3115550000",
                "Bob",
                null,
                null,
                List.of(new CreateOrderCommand.Item(productId, 10, new BigDecimal("30.00")))
        );
        OrderAppResponse orderRes = facade.placeCustomerOrder(orderCmd);
        assertEquals(OrderStatus.REGISTRADO, orderRes.status());
        assertEquals(new BigDecimal("300.00"), orderRes.totalAmount());
    }
}

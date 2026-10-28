package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.application.commands.CreatePurchaseOrderCommand;
import com.logisync.sistema_cadena_suministro.application.commands.ReceivePurchaseOrderCommand;
import com.logisync.sistema_cadena_suministro.application.ports.driving.InventoryRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.ProductRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.StorageSpaceRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.SupplierRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.SupplyNeedRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.WarehouseRepository;
import com.logisync.sistema_cadena_suministro.application.responses.PurchaseOrderAppResponse;
import com.logisync.sistema_cadena_suministro.application.service.PurchaseOrderService;
import com.logisync.sistema_cadena_suministro.domain.enums.PurchaseOrderStatus;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;
import com.logisync.sistema_cadena_suministro.domain.enums.SupplyNeedStatus;
import com.logisync.sistema_cadena_suministro.domain.models.Product;
import com.logisync.sistema_cadena_suministro.domain.models.StorageSpace;
import com.logisync.sistema_cadena_suministro.domain.models.Supplier;
import com.logisync.sistema_cadena_suministro.domain.models.SupplyNeed;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class PurchaseOrderIntegrationTest {

    private static final AtomicInteger SEQ = new AtomicInteger();

    @Autowired
    PurchaseOrderService purchaseOrderService;
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
    SupplyNeedRepository supplyNeedRepository;

    Integer supplierId;
    Integer warehouseId;
    Integer locationId;
    Integer productId;

    @BeforeEach
    void setUp() {
        int n = SEQ.incrementAndGet();
        Supplier supplier = supplierRepository.create(new Supplier(null, "PO-NIT-" + n, "Supplier PO " + n, "300", "p" + n + "@s.co", null));
        supplierId = supplier.getId();

        Warehouse wh = warehouseRepository.create(new Warehouse(null, "PO-WH-" + n, "Addr", "300", SoftDeleteStatus.ACTIVE, 1, null, null));
        warehouseId = wh.getId();

        StorageSpace zone = storageSpaceRepository.create(StorageSpace.create("Z", warehouseId, StorageSpaceType.ZONE, null, null));
        StorageSpace rack = storageSpaceRepository.create(StorageSpace.create("R", warehouseId, StorageSpaceType.RACK, null, zone));
        locationId = storageSpaceRepository.create(StorageSpace.create("L", warehouseId, StorageSpaceType.LOCATION, 200, rack)).getId();

        Product product = new Product();
        product.setName("PO Product " + n);
        product.setSupplierId(supplierId);
        product.setSku("PO-SKU-" + n);
        product.setSalePrice(new BigDecimal("15.00"));
        product.setWeightKg(new BigDecimal("1.0"));
        product.setReorderPoint(5);
        productId = productRepository.create(product).getId();
    }

    @Test
    void createPurchaseOrderCoversOpenSupplyNeedAndReceptionIncreasesStock() {
        // Pre-create an open supply need
        SupplyNeed need = new SupplyNeed(null, productId, 10, com.logisync.sistema_cadena_suministro.domain.enums.SupplyNeedOrigin.LOW_STOCK,
                SupplyNeedStatus.OPEN, null, null, null);
        need = supplyNeedRepository.save(need);
        assertTrue(supplyNeedRepository.existsOpenByProduct(productId));

        // Create PO
        CreatePurchaseOrderCommand createCommand = new CreatePurchaseOrderCommand(
                supplierId,
                warehouseId,
                List.of(new CreatePurchaseOrderCommand.DetailItem(productId, 10, new BigDecimal("8.00")))
        );
        PurchaseOrderAppResponse created = purchaseOrderService.create(createCommand);
        assertEquals(PurchaseOrderStatus.REGISTRADA, created.status());

        // Verify supply need was covered via Mediator
        SupplyNeed updatedNeed = supplyNeedRepository.findById(need.getId()).orElseThrow();
        assertEquals(SupplyNeedStatus.COVERED, updatedNeed.getStatus());
        assertEquals(created.id(), updatedNeed.getPurchaseOrderId());

        // Partial reception
        ReceivePurchaseOrderCommand receivePartial = new ReceivePurchaseOrderCommand(
                created.id(),
                List.of(new ReceivePurchaseOrderCommand.ReceptionDetail(productId, locationId, 4))
        );
        PurchaseOrderAppResponse partial = purchaseOrderService.receive(receivePartial);
        assertEquals(PurchaseOrderStatus.RECIBIDA_PARCIAL, partial.status());
        assertEquals(4, inventoryRepository.totalsByProduct(productId).physical());

        // Full reception
        ReceivePurchaseOrderCommand receiveFull = new ReceivePurchaseOrderCommand(
                created.id(),
                List.of(new ReceivePurchaseOrderCommand.ReceptionDetail(productId, locationId, 6))
        );
        PurchaseOrderAppResponse full = purchaseOrderService.receive(receiveFull);
        assertEquals(PurchaseOrderStatus.RECIBIDA, full.status());
        assertEquals(10, inventoryRepository.totalsByProduct(productId).physical());
    }
}

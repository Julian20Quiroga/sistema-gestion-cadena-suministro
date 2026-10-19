package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.application.commands.RegisterAdjustmentCommand;
import com.logisync.sistema_cadena_suministro.application.commands.RegisterEntryCommand;
import com.logisync.sistema_cadena_suministro.application.commands.RegisterExitCommand;
import com.logisync.sistema_cadena_suministro.application.ports.driving.InventoryRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.ProductRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.StorageSpaceRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.SupplierRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.WarehouseRepository;
import com.logisync.sistema_cadena_suministro.application.responses.MovementAppResponse;
import com.logisync.sistema_cadena_suministro.application.service.InventoryMovementService;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ConflictException;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.inventory.MovementStatus;
import com.logisync.sistema_cadena_suministro.domain.models.Product;
import com.logisync.sistema_cadena_suministro.domain.models.StorageSpace;
import com.logisync.sistema_cadena_suministro.domain.models.Supplier;
import com.logisync.sistema_cadena_suministro.domain.models.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class InventoryPersistenceIntegrationTest {

    private static final AtomicInteger SEQ = new AtomicInteger();

    @Autowired
    InventoryMovementService movementService;
    @Autowired
    InventoryRepository inventoryRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    SupplierRepository supplierRepository;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    StorageSpaceRepository storageSpaceRepository;

    Integer productId;
    Integer locationId;

    @BeforeEach
    void fixture() {
        int n = SEQ.incrementAndGet();
        Supplier supplier = supplierRepository.create(new Supplier(null, "NIT-" + n, "Supplier " + n, "300", "s" + n
                + "@x.co", null));
        Product product = new Product();
        product.setName("Product " + n);
        product.setSupplierId(supplier.getId());
        product.setSku("INV-SKU-" + n);
        product.setSalePrice(new BigDecimal("10.00"));
        product.setWeightKg(new BigDecimal("1.5"));
        product.setReorderPoint(0);
        productId = productRepository.create(product).getId();

        Warehouse warehouse = new Warehouse(null, "WH " + n, "Street", "300", SoftDeleteStatus.ACTIVE, 1, null, null);
        Integer warehouseId = warehouseRepository.create(warehouse).getId();
        StorageSpace zone = storageSpaceRepository
                .create(StorageSpace.create("Z", warehouseId, StorageSpaceType.ZONE, null, null));
        StorageSpace rack = storageSpaceRepository
                .create(StorageSpace.create("R", warehouseId, StorageSpaceType.RACK, null, zone));
        locationId = storageSpaceRepository
                .create(StorageSpace.create("L", warehouseId, StorageSpaceType.LOCATION, 100, rack)).getId();
    }

    @Test
    void entryThenExitThenUndoKeepsStockConsistent() {
        movementService.registerEntry(new RegisterEntryCommand(locationId, productId, 20, "PO-1"));
        MovementAppResponse exit = movementService.registerExit(new RegisterExitCommand(locationId, productId, 8, "ORD-1"));
        assertEquals(12, inventoryRepository.totalsByProduct(productId).physical());

        MovementAppResponse undone = movementService.undo(exit.id());
        assertEquals(MovementStatus.REVERSED, undone.status());
        assertEquals(20, inventoryRepository.totalsByProduct(productId).physical());
        assertThrows(ConflictException.class, () -> movementService.undo(exit.id()));
    }

    @Test
    void exitBeyondAvailableStockIsRejectedAndRollsBack() {
        movementService.registerEntry(new RegisterEntryCommand(locationId, productId, 5, null));

        assertThrows(BusinessRuleException.class,
                () -> movementService.registerExit(new RegisterExitCommand(locationId, productId, 6, null)));
        assertEquals(5, inventoryRepository.totalsByProduct(productId).physical());
    }

    @Test
    void adjustmentWithoutReasonIsRejected() {
        movementService.registerEntry(new RegisterEntryCommand(locationId, productId, 5, null));

        assertThrows(BusinessRuleException.class,
                () -> movementService.registerAdjustment(new RegisterAdjustmentCommand(locationId, productId, -1, "")));
    }

    @Test
    void staleVersionIsRejectedByOptimisticLocking() {
        movementService.registerEntry(new RegisterEntryCommand(locationId, productId, 10, null));
        InventoryItem first = inventoryRepository.findBySpaceAndProduct(locationId, productId).orElseThrow();
        InventoryItem second = inventoryRepository.findBySpaceAndProduct(locationId, productId).orElseThrow();

        first.apply(-1, 0);
        inventoryRepository.save(first);

        second.apply(-2, 0);
        assertThrows(ObjectOptimisticLockingFailureException.class, () -> inventoryRepository.save(second));
        assertEquals(9, inventoryRepository.totalsByProduct(productId).physical());
    }
}

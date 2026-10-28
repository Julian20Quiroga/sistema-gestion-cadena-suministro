package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.application.commands.CreateLayoutSnapshotCommand;
import com.logisync.sistema_cadena_suministro.application.ports.driven.ManageLayoutSnapshotUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.OptimizeWarehouseLayoutUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.StorageSpaceRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.WarehouseRepository;
import com.logisync.sistema_cadena_suministro.application.responses.LayoutOptimizationAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.LayoutSnapshotAppResponse;
import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;
import com.logisync.sistema_cadena_suministro.domain.models.StorageSpace;
import com.logisync.sistema_cadena_suministro.domain.models.Warehouse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class WarehouseOptimizationIntegrationTest {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private StorageSpaceRepository storageSpaceRepository;

    @Autowired
    private OptimizeWarehouseLayoutUseCase optimizeWarehouseLayoutUseCase;

    @Autowired
    private ManageLayoutSnapshotUseCase manageLayoutSnapshotUseCase;

    @Test
    void snapshotAndOptimizeWarehouseLayout() {
        Warehouse wh = warehouseRepository.create(new Warehouse(
                null, "WH Opt", "Addr", "300",
                com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus.ACTIVE, null, null, null
        ));

        StorageSpace zone = storageSpaceRepository.create(StorageSpace.create("Zone A", wh.getId(), StorageSpaceType.ZONE, null, null));
        StorageSpace rack = storageSpaceRepository.create(StorageSpace.create("Rack 01", wh.getId(), StorageSpaceType.RACK, null, zone));
        StorageSpace loc1 = storageSpaceRepository.create(StorageSpace.create("Loc 01", wh.getId(), StorageSpaceType.LOCATION, 500, rack));
        StorageSpace loc2 = storageSpaceRepository.create(StorageSpace.create("Loc 02", wh.getId(), StorageSpaceType.LOCATION, 500, rack));

        // 1. Create Memento Snapshot manually
        LayoutSnapshotAppResponse snap = manageLayoutSnapshotUseCase.createSnapshot(
                new CreateLayoutSnapshotCommand(wh.getId(), "Initial State")
        );
        assertNotNull(snap.id());
        assertEquals("Initial State", snap.snapshotName());

        // 2. List snapshots
        List<LayoutSnapshotAppResponse> list = manageLayoutSnapshotUseCase.listSnapshots(wh.getId());
        assertFalse(list.isEmpty());

        // 3. Optimize layout (generates auto snapshot)
        LayoutOptimizationAppResponse opt = optimizeWarehouseLayoutUseCase.optimize(wh.getId(), true);
        assertNotNull(opt.preOptimizationSnapshotId());
        assertNotNull(opt.summary());

        // 4. Restore from initial snapshot
        LayoutSnapshotAppResponse restored = manageLayoutSnapshotUseCase.restoreSnapshot(wh.getId(), snap.id());
        assertEquals(snap.id(), restored.id());
    }
}

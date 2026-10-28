package com.logisync.sistema_cadena_suministro.domain.memento.storage;

import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;
import com.logisync.sistema_cadena_suministro.domain.models.StorageSpace;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StorageLayoutMementoTest {

    @Test
    void originatorSavesAndRestoresMementoSnapshot() {
        WarehouseLayoutOriginator originator = new WarehouseLayoutOriginator();

        StorageSpace s1 = new StorageSpace(1, "Zone A", 1, 0, com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus.ACTIVE, null, StorageSpaceType.ZONE);
        StorageSpace s2 = new StorageSpace(2, "Rack 01", 1, 0, com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus.ACTIVE, 1, StorageSpaceType.RACK);
        List<StorageSpace> originalSpaces = List.of(s1, s2);

        originator.setState(1, originalSpaces);
        StorageLayoutMemento memento = originator.saveToMemento("Baseline Layout");

        assertNotNull(memento);
        assertEquals(1, memento.getWarehouseId());
        assertEquals("Baseline Layout", memento.getSnapshotName());
        assertNotNull(memento.getLayoutDataJson());

        // Restore in a fresh originator
        WarehouseLayoutOriginator restorer = new WarehouseLayoutOriginator();
        List<StorageSpace> restored = restorer.restoreFromMemento(memento);

        assertEquals(2, restored.size());
        assertEquals("Zone A", restored.get(0).getName());
        assertEquals("Rack 01", restored.get(1).getName());
    }
}

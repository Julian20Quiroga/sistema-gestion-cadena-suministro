package com.logisync.sistema_cadena_suministro.domain.models;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StorageSpaceTest {

    private StorageSpace zone() {
        return new StorageSpace(1, "Z", 1, 0, SoftDeleteStatus.ACTIVE, null, StorageSpaceType.ZONE);
    }

    @Test
    void zoneIsCreatedAtWarehouseLevelWithZeroCapacity() {
        StorageSpace zone = StorageSpace.create("Z", 1, StorageSpaceType.ZONE, 99, null);

        assertEquals(0, zone.getCapacity());
        assertEquals(SoftDeleteStatus.ACTIVE, zone.getStatus());
    }

    @Test
    void locationCannotBeCreatedDirectlyUnderZone() {
        assertThrows(BusinessRuleException.class,
                () -> StorageSpace.create("L", 1, StorageSpaceType.LOCATION, 10, zone()));
    }

    @Test
    void locationRequiresPositiveCapacity() {
        StorageSpace rack = StorageSpace.create("R", 1, StorageSpaceType.RACK, null, zone());
        rack.setId(2);

        assertThrows(BusinessRuleException.class,
                () -> StorageSpace.create("L", 1, StorageSpaceType.LOCATION, null, rack));
        assertEquals(10, StorageSpace.create("L", 1, StorageSpaceType.LOCATION, 10, rack).getCapacity());
    }

    @Test
    void capacityCannotDropBelowStoredUnits() {
        StorageSpace location = new StorageSpace(3, "L", 1, 100, SoftDeleteStatus.ACTIVE, 2,
                StorageSpaceType.LOCATION);

        assertThrows(BusinessRuleException.class, () -> location.rename("L", 20, 50));
        location.rename("L2", 80, 50);
        assertEquals(80, location.getCapacity());
    }
}

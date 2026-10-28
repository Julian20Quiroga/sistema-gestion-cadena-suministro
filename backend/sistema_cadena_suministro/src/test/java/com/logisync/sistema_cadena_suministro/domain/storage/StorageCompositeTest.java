package com.logisync.sistema_cadena_suministro.domain.storage;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;
import com.logisync.sistema_cadena_suministro.domain.models.StorageSpace;
import com.logisync.sistema_cadena_suministro.domain.models.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StorageCompositeTest {

    WarehouseNode root;

    @BeforeEach
    void setUp() {
        Warehouse warehouse = new Warehouse(1, "Main", "addr", "1", SoftDeleteStatus.ACTIVE, 1, null, null);
        List<StorageSpace> spaces = List.of(
                new StorageSpace(10, "Z1", 1, 0, SoftDeleteStatus.ACTIVE, null, StorageSpaceType.ZONE),
                new StorageSpace(11, "R1", 1, 0, SoftDeleteStatus.ACTIVE, 10, StorageSpaceType.RACK),
                new StorageSpace(12, "L1", 1, 100, SoftDeleteStatus.ACTIVE, 11, StorageSpaceType.LOCATION),
                new StorageSpace(13, "L2", 1, 50, SoftDeleteStatus.ACTIVE, 11, StorageSpaceType.LOCATION),
                new StorageSpace(14, "Z2", 1, 0, SoftDeleteStatus.ACTIVE, null, StorageSpaceType.ZONE));
        root = StorageTreeAssembler.assemble(warehouse, spaces,
                Map.of(12, List.of(new StoredItem(1, 60)), 13, List.of(new StoredItem(2, 50))));
    }

    @Test
    void capacityAndOccupancyAreComputedRecursively() {
        assertEquals(150, root.capacity());
        assertEquals(110, root.occupancy());
        assertEquals(40, root.freeCapacity());
    }

    @Test
    void iteratorWalksTreeDepthFirstPreOrder() {
        List<String> names = new ArrayList<>();
        root.forEach(node -> names.add(node.name()));

        assertEquals(List.of("Main", "Z1", "R1", "L1", "L2", "Z2"), names);
    }

    @Test
    void freeLocationVisitorReturnsOnlyLocationsWithEnoughSpace() {
        List<LocationNode> free = root.accept(new FreeLocationVisitor(30));

        assertEquals(1, free.size());
        assertEquals("L1", free.get(0).name());
    }

    @Test
    void structureViewVisitorBuildsNestedViewWithUsage() {
        StorageNodeView view = root.accept(new StructureViewVisitor());

        assertEquals(StorageSpaceType.WAREHOUSE, view.type());
        assertEquals(2, view.children().size());
        StorageNodeView rack = view.children().get(0).children().get(0);
        assertEquals(110, rack.occupancy());
        assertEquals(73.33, rack.usagePercent());
        assertTrue(rack.children().get(0).children().isEmpty());
    }
}

package com.logisync.sistema_cadena_suministro.domain.storage;

/** Visitor: builds a nested view with capacity and occupancy of every node. */
public class StructureViewVisitor implements StorageNodeVisitor<StorageNodeView> {

    @Override
    public StorageNodeView visitWarehouse(WarehouseNode warehouse) {
        return container(warehouse);
    }

    @Override
    public StorageNodeView visitZone(ZoneNode zone) {
        return container(zone);
    }

    @Override
    public StorageNodeView visitRack(RackNode rack) {
        return container(rack);
    }

    @Override
    public StorageNodeView visitLocation(LocationNode location) {
        return StorageNodeView.of(location, java.util.List.of());
    }

    private StorageNodeView container(StorageContainer node) {
        return StorageNodeView.of(node, node.children().stream().map(child -> child.accept(this)).toList());
    }
}

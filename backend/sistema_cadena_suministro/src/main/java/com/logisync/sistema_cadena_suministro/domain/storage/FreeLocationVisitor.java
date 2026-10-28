package com.logisync.sistema_cadena_suministro.domain.storage;

import java.util.List;

/** Visitor: finds the locations able to take at least a number of additional units. */
public class FreeLocationVisitor implements StorageNodeVisitor<List<LocationNode>> {

    private final long minFreeUnits;

    public FreeLocationVisitor(long minFreeUnits) {
        this.minFreeUnits = minFreeUnits;
    }

    @Override
    public List<LocationNode> visitWarehouse(WarehouseNode warehouse) {
        return collect(warehouse);
    }

    @Override
    public List<LocationNode> visitZone(ZoneNode zone) {
        return collect(zone);
    }

    @Override
    public List<LocationNode> visitRack(RackNode rack) {
        return collect(rack);
    }

    @Override
    public List<LocationNode> visitLocation(LocationNode location) {
        return location.canStore(minFreeUnits) ? List.of(location) : List.of();
    }

    private List<LocationNode> collect(StorageContainer node) {
        return node.children().stream().flatMap(child -> child.accept(this).stream()).toList();
    }
}

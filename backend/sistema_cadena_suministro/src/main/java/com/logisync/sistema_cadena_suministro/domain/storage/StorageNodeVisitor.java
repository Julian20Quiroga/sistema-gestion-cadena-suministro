package com.logisync.sistema_cadena_suministro.domain.storage;

/** Visitor: new operations over the storage tree without modifying its node classes. */
public interface StorageNodeVisitor<R> {

    R visitWarehouse(WarehouseNode warehouse);

    R visitZone(ZoneNode zone);

    R visitRack(RackNode rack);

    R visitLocation(LocationNode location);
}

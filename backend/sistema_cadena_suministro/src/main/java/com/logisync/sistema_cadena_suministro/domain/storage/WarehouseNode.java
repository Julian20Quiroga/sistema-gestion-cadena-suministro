package com.logisync.sistema_cadena_suministro.domain.storage;

import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;

/** Composite root of the storage tree. */
public class WarehouseNode extends StorageContainer {

    public WarehouseNode(Integer id, String name) {
        super(id, name);
    }

    @Override
    public StorageSpaceType type() {
        return StorageSpaceType.WAREHOUSE;
    }

    @Override
    public <R> R accept(StorageNodeVisitor<R> visitor) {
        return visitor.visitWarehouse(this);
    }
}

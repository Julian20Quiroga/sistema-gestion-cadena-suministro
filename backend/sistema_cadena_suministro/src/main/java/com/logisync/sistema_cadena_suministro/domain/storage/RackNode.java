package com.logisync.sistema_cadena_suministro.domain.storage;

import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;

public class RackNode extends StorageContainer {

    public RackNode(Integer id, String name) {
        super(id, name);
    }

    @Override
    public StorageSpaceType type() {
        return StorageSpaceType.RACK;
    }

    @Override
    public <R> R accept(StorageNodeVisitor<R> visitor) {
        return visitor.visitRack(this);
    }
}

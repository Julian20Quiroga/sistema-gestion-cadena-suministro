package com.logisync.sistema_cadena_suministro.domain.storage;

import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;

public class ZoneNode extends StorageContainer {

    public ZoneNode(Integer id, String name) {
        super(id, name);
    }

    @Override
    public StorageSpaceType type() {
        return StorageSpaceType.ZONE;
    }

    @Override
    public <R> R accept(StorageNodeVisitor<R> visitor) {
        return visitor.visitZone(this);
    }
}

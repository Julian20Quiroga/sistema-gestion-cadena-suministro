package com.logisync.sistema_cadena_suministro.domain.enums;

/** Levels of the physical warehouse hierarchy: Warehouse > Zone > Rack > Location (leaf). */
public enum StorageSpaceType {
    WAREHOUSE(null),
    ZONE(WAREHOUSE),
    RACK(ZONE),
    LOCATION(RACK);

    private final StorageSpaceType parentType;

    StorageSpaceType(StorageSpaceType parentType) {
        this.parentType = parentType;
    }

    public boolean canBeChildOf(StorageSpaceType parent) {
        return parentType != null && parentType == parent;
    }

    public StorageSpaceType requiredParent() {
        return parentType;
    }

    public boolean isLeaf() {
        return this == LOCATION;
    }
}

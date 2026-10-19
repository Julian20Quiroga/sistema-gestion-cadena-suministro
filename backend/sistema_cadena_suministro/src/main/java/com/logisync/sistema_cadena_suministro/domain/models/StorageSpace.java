package com.logisync.sistema_cadena_suministro.domain.models;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorageSpace {
    private Integer id;
    private String name;
    private Integer warehouseId;
    private Integer capacity;
    private SoftDeleteStatus status;
    private Integer parentId;
    private StorageSpaceType type;

    public boolean isActive() {
        return status == SoftDeleteStatus.ACTIVE;
    }

    /** Builds a space validating the hierarchy rule against its (optional) parent; only locations carry capacity. */
    public static StorageSpace create(String name, Integer warehouseId, StorageSpaceType type, Integer capacity,
            StorageSpace parent) {
        StorageSpaceType parentType = parent == null ? StorageSpaceType.WAREHOUSE : parent.getType();
        if (!type.canBeChildOf(parentType)) {
            throw new BusinessRuleException(type + " cannot be placed under " + parentType);
        }
        if (parent != null && (!parent.isActive() || !parent.getWarehouseId().equals(warehouseId))) {
            throw new BusinessRuleException("Parent space must be active and belong to the same warehouse");
        }
        int effectiveCapacity = type.isLeaf() && capacity != null ? capacity : 0;
        if (type.isLeaf() && effectiveCapacity <= 0) {
            throw new BusinessRuleException("A LOCATION requires a positive capacity");
        }
        return new StorageSpace(null, name, warehouseId, effectiveCapacity, SoftDeleteStatus.ACTIVE,
                parent == null ? null : parent.getId(), type);
    }

    public void rename(String newName, Integer newCapacity, long currentUnits) {
        this.name = newName;
        if (type.isLeaf()) {
            if (newCapacity == null || newCapacity <= 0) {
                throw new BusinessRuleException("A LOCATION requires a positive capacity");
            }
            if (newCapacity < currentUnits) {
                throw new BusinessRuleException("Capacity cannot be lower than the units currently stored ("
                        + currentUnits + ")");
            }
            this.capacity = newCapacity;
        }
    }
}

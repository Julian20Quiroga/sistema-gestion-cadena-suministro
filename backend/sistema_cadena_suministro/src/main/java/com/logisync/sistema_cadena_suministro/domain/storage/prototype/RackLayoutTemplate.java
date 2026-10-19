package com.logisync.sistema_cadena_suministro.domain.storage.prototype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Prototype: Concrete prototype representing a reusable rack layout template that can be deep-cloned.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RackLayoutTemplate implements StorageSpacePrototype {

    private String name;
    private Integer warehouseId;
    private int numberOfLevels;
    private int capacityPerLevel;
    private List<String> locationNames = new ArrayList<>();

    @Override
    public RackLayoutTemplate cloneSpace(String newName, Integer newCapacity) {
        List<String> clonedLocations = new ArrayList<>(this.locationNames);
        int effectiveCapacity = newCapacity != null ? newCapacity : this.capacityPerLevel;
        return new RackLayoutTemplate(newName, this.warehouseId, this.numberOfLevels, effectiveCapacity, clonedLocations);
    }
}

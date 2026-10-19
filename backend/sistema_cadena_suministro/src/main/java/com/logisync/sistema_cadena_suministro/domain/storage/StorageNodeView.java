package com.logisync.sistema_cadena_suministro.domain.storage;

import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;

import java.util.List;

public record StorageNodeView(Integer id, StorageSpaceType type, String name, long capacity, long occupancy,
        double usagePercent, List<StorageNodeView> children) {

    static StorageNodeView of(StorageNode node, List<StorageNodeView> children) {
        double usage = node.capacity() == 0 ? 0 : Math.round(node.occupancy() * 10000.0 / node.capacity()) / 100.0;
        return new StorageNodeView(node.id(), node.type(), node.name(), node.capacity(), node.occupancy(), usage,
                children);
    }
}

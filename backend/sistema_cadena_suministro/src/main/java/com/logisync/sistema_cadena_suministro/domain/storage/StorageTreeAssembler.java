package com.logisync.sistema_cadena_suministro.domain.storage;

import com.logisync.sistema_cadena_suministro.domain.models.StorageSpace;
import com.logisync.sistema_cadena_suministro.domain.models.Warehouse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Builds the composite tree of a warehouse from its flat active spaces and current stock. */
public final class StorageTreeAssembler {

    private StorageTreeAssembler() {
    }

    public static WarehouseNode assemble(Warehouse warehouse, List<StorageSpace> spaces,
            Map<Integer, List<StoredItem>> itemsBySpace) {
        WarehouseNode root = new WarehouseNode(warehouse.getId(), warehouse.getName());
        Map<Integer, StorageContainer> containers = new HashMap<>();
        for (StorageSpace space : spaces) {
            StorageContainer container = switch (space.getType()) {
                case ZONE -> new ZoneNode(space.getId(), space.getName());
                case RACK -> new RackNode(space.getId(), space.getName());
                default -> null;
            };
            if (container != null) {
                containers.put(space.getId(), container);
            }
        }
        for (StorageSpace space : spaces) {
            StorageContainer parent = space.getParentId() == null ? root : containers.get(space.getParentId());
            StorageNode node = containers.get(space.getId());
            if (node == null && space.getType().isLeaf()) {
                node = new LocationNode(space.getId(), space.getName(), space.getCapacity(),
                        itemsBySpace.getOrDefault(space.getId(), List.of()));
            }
            if (parent != null && node != null) {
                parent.add(node);
            }
        }
        return root;
    }
}

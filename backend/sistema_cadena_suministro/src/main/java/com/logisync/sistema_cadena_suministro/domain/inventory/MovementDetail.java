package com.logisync.sistema_cadena_suministro.domain.inventory;

public record MovementDetail(Integer inventoryId, Integer productId, Integer storageSpaceId, int quantity,
        int reservedDelta) {
}

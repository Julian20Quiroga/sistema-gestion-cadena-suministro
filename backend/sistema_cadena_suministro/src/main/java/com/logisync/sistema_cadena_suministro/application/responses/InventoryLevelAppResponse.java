package com.logisync.sistema_cadena_suministro.application.responses;

public record InventoryLevelAppResponse(Integer id, Integer productId, Integer storageSpaceId, Integer warehouseId,
        int physicalQuantity, int reservedQuantity, int availableQuantity) {
}

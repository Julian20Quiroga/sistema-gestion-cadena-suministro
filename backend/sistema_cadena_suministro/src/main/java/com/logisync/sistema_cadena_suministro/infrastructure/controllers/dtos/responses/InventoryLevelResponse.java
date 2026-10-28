package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

public record InventoryLevelResponse(Integer id, Integer productId, Integer storageSpaceId, Integer warehouseId,
        int physicalQuantity, int reservedQuantity, int availableQuantity) {
}

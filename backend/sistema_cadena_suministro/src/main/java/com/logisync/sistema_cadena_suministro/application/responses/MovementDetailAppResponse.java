package com.logisync.sistema_cadena_suministro.application.responses;

public record MovementDetailAppResponse(Integer inventoryId, Integer productId, Integer storageSpaceId,
        int quantity, int reservedDelta) {
}

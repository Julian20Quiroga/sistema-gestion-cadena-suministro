package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

public record MovementDetailResponse(Integer inventoryId, Integer productId, Integer storageSpaceId, int quantity,
        int reservedDelta) {
}

package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

public record DetailDispatchResponse(
        Integer id,
        Integer detailOrderId,
        Integer storageSpaceId,
        Integer inventoryId,
        int quantity
) {}

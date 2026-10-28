package com.logisync.sistema_cadena_suministro.application.responses;

public record DetailDispatchAppResponse(
        Integer id,
        Integer detailOrderId,
        Integer storageSpaceId,
        Integer inventoryId,
        int quantity
) {}

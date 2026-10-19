package com.logisync.sistema_cadena_suministro.application.responses;

public record StockAllocationAppResponse(Integer inventoryId, Integer storageSpaceId, Integer productId,
        int quantity) {
}

package com.logisync.sistema_cadena_suministro.domain.inventory;

public record StockAllocation(Integer inventoryId, Integer storageSpaceId, Integer productId, int quantity) {
}

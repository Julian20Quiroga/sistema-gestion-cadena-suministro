package com.logisync.sistema_cadena_suministro.application.responses;

public record ProductAvailabilityAppResponse(Integer productId, long physicalQuantity, long reservedQuantity,
        long availableQuantity, int reorderPoint, boolean belowReorderPoint) {
}

package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

public record ProductAvailabilityResponse(Integer productId, long physicalQuantity, long reservedQuantity,
        long availableQuantity, int reorderPoint, boolean belowReorderPoint) {
}

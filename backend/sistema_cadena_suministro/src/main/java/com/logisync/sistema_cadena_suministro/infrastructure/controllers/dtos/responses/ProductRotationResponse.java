package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

public record ProductRotationResponse(Integer productId, int days, long unitsExited, long currentPhysicalQuantity,
        double rotationIndex) {
}

package com.logisync.sistema_cadena_suministro.application.responses;

public record ProductRotationAppResponse(Integer productId, int days, long unitsExited, long currentPhysicalQuantity,
        double rotationIndex) {
}

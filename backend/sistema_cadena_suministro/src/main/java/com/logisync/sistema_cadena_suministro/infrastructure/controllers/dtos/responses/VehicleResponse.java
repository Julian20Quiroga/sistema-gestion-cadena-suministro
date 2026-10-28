package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;

import java.math.BigDecimal;

public record VehicleResponse(
        Integer id,
        String plate,
        String model,
        BigDecimal capacityKg,
        BigDecimal averageSpeedKmh,
        SoftDeleteStatus status,
        String rawApiKey
) {}

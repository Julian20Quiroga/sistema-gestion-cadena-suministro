package com.logisync.sistema_cadena_suministro.application.responses;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;

import java.math.BigDecimal;

public record VehicleAppResponse(
        Integer id,
        String plate,
        String model,
        BigDecimal capacityKg,
        BigDecimal averageSpeedKmh,
        SoftDeleteStatus status,
        String rawApiKey
) {}

package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TelemetryReadingResponse(
        Integer id,
        Integer vehicleId,
        String vehiclePlate,
        BigDecimal latitude,
        BigDecimal longitude,
        double speedKmh,
        LocalDateTime recordedAt,
        LocalDateTime createdAt
) {}

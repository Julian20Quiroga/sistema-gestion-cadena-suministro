package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IngestTelemetryRequest(
        @NotNull(message = "Latitude is required")
        @DecimalMin(value = "-90.0", message = "Latitude min is -90")
        @DecimalMax(value = "90.0", message = "Latitude max is 90")
        BigDecimal latitude,

        @NotNull(message = "Longitude is required")
        @DecimalMin(value = "-180.0", message = "Longitude min is -180")
        @DecimalMax(value = "180.0", message = "Longitude max is 180")
        BigDecimal longitude,

        @DecimalMin(value = "0.0", message = "Speed cannot be negative")
        double speedKmh,

        @NotNull(message = "Recorded at timestamp is required")
        LocalDateTime recordedAt
) {}

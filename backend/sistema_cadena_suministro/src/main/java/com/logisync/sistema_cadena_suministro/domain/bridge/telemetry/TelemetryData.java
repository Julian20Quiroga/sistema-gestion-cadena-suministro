package com.logisync.sistema_cadena_suministro.domain.bridge.telemetry;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TelemetryData(
        String deviceId,
        BigDecimal latitude,
        BigDecimal longitude,
        double speedKmh,
        LocalDateTime timestamp
) {}

package com.logisync.sistema_cadena_suministro.application.commands;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IngestTelemetryCommand(
        BigDecimal latitude,
        BigDecimal longitude,
        double speedKmh,
        LocalDateTime recordedAt
) {}

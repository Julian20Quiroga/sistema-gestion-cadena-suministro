package com.logisync.sistema_cadena_suministro.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryReading {
    private Integer id;
    private Integer vehicleId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private double speedKmh;
    private LocalDateTime recordedAt;
    private LocalDateTime createdAt;
}

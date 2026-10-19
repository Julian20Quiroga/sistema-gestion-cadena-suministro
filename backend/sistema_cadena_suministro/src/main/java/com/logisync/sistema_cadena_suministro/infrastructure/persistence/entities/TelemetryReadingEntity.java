package com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "telemetry_readings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryReadingEntity extends BaseEntity {

    @Column(name = "vehicle_id", nullable = false)
    private Integer vehicleId;

    @Column(name = "latitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "speed_kmh", nullable = false)
    private double speedKmh;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;
}

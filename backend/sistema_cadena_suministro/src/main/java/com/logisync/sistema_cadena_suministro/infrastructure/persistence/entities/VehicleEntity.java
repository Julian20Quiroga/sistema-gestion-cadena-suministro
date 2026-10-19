package com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleEntity extends BaseEntity {

    @Column(name = "plate", nullable = false, unique = true, length = 20)
    private String plate;

    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Column(name = "capacity_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal capacityKg;

    @Column(name = "average_speed_kmh", nullable = false, precision = 5, scale = 2)
    private BigDecimal averageSpeedKmh = new BigDecimal("40.00");

    @Column(name = "api_key_hash", nullable = false, unique = true, length = 128)
    private String apiKeyHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SoftDeleteStatus status = SoftDeleteStatus.ACTIVE;
}

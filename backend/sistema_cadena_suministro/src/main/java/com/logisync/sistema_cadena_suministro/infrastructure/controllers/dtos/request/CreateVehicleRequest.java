package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateVehicleRequest(
        @NotBlank String plate,
        @NotBlank String model,
        @NotNull @Positive BigDecimal capacityKg,
        @Positive BigDecimal averageSpeedKmh
) {}

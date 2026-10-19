package com.logisync.sistema_cadena_suministro.application.commands;

import java.math.BigDecimal;

public record CreateVehicleCommand(
        String plate,
        String model,
        BigDecimal capacityKg,
        BigDecimal averageSpeedKmh
) {}

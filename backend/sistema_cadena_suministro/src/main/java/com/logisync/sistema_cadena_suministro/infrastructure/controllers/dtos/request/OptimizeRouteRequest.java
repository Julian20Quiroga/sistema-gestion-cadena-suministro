package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OptimizeRouteRequest(
        @NotNull Integer warehouseId,
        @NotNull Integer vehicleId,
        @NotEmpty List<Integer> orderIds,
        String strategyName
) {}

package com.logisync.sistema_cadena_suministro.application.commands;

import java.util.List;

public record OptimizeRouteCommand(
        Integer warehouseId,
        Integer vehicleId,
        List<Integer> orderIds,
        String strategyName
) {}

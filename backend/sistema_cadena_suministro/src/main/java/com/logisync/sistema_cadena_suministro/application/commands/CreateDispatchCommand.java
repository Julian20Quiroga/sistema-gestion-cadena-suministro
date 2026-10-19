package com.logisync.sistema_cadena_suministro.application.commands;

public record CreateDispatchCommand(
        Integer orderId,
        Integer warehouseId,
        Integer vehicleId
) {}

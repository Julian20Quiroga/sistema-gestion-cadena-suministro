package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request;

import jakarta.validation.constraints.NotNull;

public record CreateDispatchRequest(
        @NotNull Integer orderId,
        @NotNull Integer warehouseId,
        Integer vehicleId
) {}

package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

import com.logisync.sistema_cadena_suministro.domain.enums.DispatchStatus;

import java.util.List;

public record DispatchResponse(
        Integer id,
        Integer orderId,
        Integer warehouseId,
        Integer vehicleId,
        DispatchStatus status,
        List<DetailDispatchResponse> details
) {}

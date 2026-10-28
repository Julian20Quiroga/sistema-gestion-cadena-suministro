package com.logisync.sistema_cadena_suministro.application.responses;

import com.logisync.sistema_cadena_suministro.domain.enums.DispatchStatus;

import java.util.List;

public record DispatchAppResponse(
        Integer id,
        Integer orderId,
        Integer warehouseId,
        Integer vehicleId,
        DispatchStatus status,
        List<DetailDispatchAppResponse> details
) {}

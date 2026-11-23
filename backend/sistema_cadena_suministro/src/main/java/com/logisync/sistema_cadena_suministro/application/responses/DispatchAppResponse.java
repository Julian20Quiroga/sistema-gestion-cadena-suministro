package com.logisync.sistema_cadena_suministro.application.responses;

import com.logisync.sistema_cadena_suministro.domain.enums.DispatchStatus;

import java.util.List;
import java.time.LocalDateTime;

public record DispatchAppResponse(
        Integer id,
        LocalDateTime createdAt,
        Integer orderId,
        Integer warehouseId,
        Integer vehicleId,
        DispatchStatus status,
        List<DetailDispatchAppResponse> details
) {}

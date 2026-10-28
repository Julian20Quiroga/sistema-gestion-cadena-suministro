package com.logisync.sistema_cadena_suministro.application.responses;

import com.logisync.sistema_cadena_suministro.domain.enums.SupplyNeedOrigin;
import com.logisync.sistema_cadena_suministro.domain.enums.SupplyNeedStatus;

import java.time.LocalDateTime;

public record SupplyNeedAppResponse(
        Integer id,
        Integer productId,
        int requestedQuantity,
        SupplyNeedOrigin origin,
        SupplyNeedStatus status,
        Integer purchaseOrderId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

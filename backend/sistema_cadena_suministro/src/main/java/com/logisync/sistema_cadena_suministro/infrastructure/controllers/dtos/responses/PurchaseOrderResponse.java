package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

import com.logisync.sistema_cadena_suministro.domain.enums.PurchaseOrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderResponse(
        Integer id,
        Integer supplierId,
        LocalDateTime orderDate,
        PurchaseOrderStatus status,
        Integer warehouseId,
        List<PurchaseOrderDetailResponse> details
) {}

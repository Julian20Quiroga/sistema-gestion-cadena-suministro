package com.logisync.sistema_cadena_suministro.application.responses;

import com.logisync.sistema_cadena_suministro.domain.enums.PurchaseOrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderAppResponse(
        Integer id,
        Integer supplierId,
        LocalDateTime orderDate,
        PurchaseOrderStatus status,
        Integer warehouseId,
        List<PurchaseOrderDetailAppResponse> details
) {}

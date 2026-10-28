package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

import java.math.BigDecimal;

public record PurchaseOrderDetailResponse(
        Integer id,
        Integer productId,
        int quantity,
        int receivedQuantity,
        BigDecimal unitPrice
) {}

package com.logisync.sistema_cadena_suministro.application.responses;

import java.math.BigDecimal;

public record PurchaseOrderDetailAppResponse(
        Integer id,
        Integer productId,
        int quantity,
        int receivedQuantity,
        BigDecimal unitPrice
) {}

package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

import java.math.BigDecimal;

public record OrderDetailResponse(
        Integer id,
        Integer productId,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}

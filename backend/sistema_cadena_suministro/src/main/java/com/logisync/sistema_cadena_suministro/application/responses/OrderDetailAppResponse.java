package com.logisync.sistema_cadena_suministro.application.responses;

import java.math.BigDecimal;

public record OrderDetailAppResponse(
        Integer id,
        Integer productId,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}

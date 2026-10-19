package com.logisync.sistema_cadena_suministro.application.responses;

import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderAppResponse(
        Integer id,
        String orderReference,
        String deliveryAddress,
        String deliveryCity,
        String deliveryCountry,
        String customerPhone,
        String customerName,
        BigDecimal deliveryLatitude,
        BigDecimal deliveryLongitude,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderDetailAppResponse> details
) {}

package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Integer id,
        LocalDateTime createdAt,
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
        List<OrderDetailResponse> details
) {}

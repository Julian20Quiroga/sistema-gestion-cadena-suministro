package com.logisync.sistema_cadena_suministro.application.commands;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderCommand(
        String orderReference,
        String deliveryAddress,
        String deliveryCity,
        String deliveryCountry,
        String customerPhone,
        String customerName,
        BigDecimal deliveryLatitude,
        BigDecimal deliveryLongitude,
        List<Item> items
) {
    public record Item(Integer productId, int quantity, BigDecimal unitPrice) {}
}

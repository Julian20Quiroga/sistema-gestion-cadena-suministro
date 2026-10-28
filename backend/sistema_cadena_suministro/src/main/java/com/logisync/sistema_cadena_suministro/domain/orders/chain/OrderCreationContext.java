package com.logisync.sistema_cadena_suministro.domain.orders.chain;

import com.logisync.sistema_cadena_suministro.domain.models.Order;
import com.logisync.sistema_cadena_suministro.domain.models.Product;

import java.util.Map;

public record OrderCreationContext(
        Order order,
        Map<Integer, Product> productsById,
        Map<Integer, Long> availableStockByProduct
) {}

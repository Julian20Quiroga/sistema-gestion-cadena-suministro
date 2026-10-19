package com.logisync.sistema_cadena_suministro.domain.orders.chain;

import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.models.OrderDetail;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Chain of Responsibility: Validates that sufficient physical unreserved stock exists.
 */
public class OrderStockAvailabilityValidator extends OrderCreationValidator {

    @Override
    protected void check(OrderCreationContext context) {
        Map<Integer, Integer> requestedPerProduct = context.order().getDetails().stream()
                .collect(Collectors.groupingBy(OrderDetail::getProductId, Collectors.summingInt(OrderDetail::getQuantity)));

        for (var entry : requestedPerProduct.entrySet()) {
            Integer prodId = entry.getKey();
            int requestedQty = entry.getValue();
            long available = context.availableStockByProduct().getOrDefault(prodId, 0L);
            if (requestedQty > available) {
                throw new BusinessRuleException("Insufficient stock for product " + prodId
                        + ". Available: " + available + ", requested: " + requestedQty);
            }
        }
    }
}

package com.logisync.sistema_cadena_suministro.domain.orders.chain;

import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.models.OrderDetail;
import com.logisync.sistema_cadena_suministro.domain.models.Product;

/**
 * Chain of Responsibility: Validates order details products exist and are active.
 */
public class OrderProductsActiveValidator extends OrderCreationValidator {

    @Override
    protected void check(OrderCreationContext context) {
        if (context.order().getDetails().isEmpty()) {
            throw new BusinessRuleException("Order must contain at least one item");
        }
        for (OrderDetail detail : context.order().getDetails()) {
            if (detail.getQuantity() <= 0) {
                throw new BusinessRuleException("Product " + detail.getProductId() + " quantity must be positive");
            }
            Product product = context.productsById().get(detail.getProductId());
            if (product == null || !product.isActive()) {
                throw new BusinessRuleException("Product " + detail.getProductId() + " is missing or inactive");
            }
        }
    }
}

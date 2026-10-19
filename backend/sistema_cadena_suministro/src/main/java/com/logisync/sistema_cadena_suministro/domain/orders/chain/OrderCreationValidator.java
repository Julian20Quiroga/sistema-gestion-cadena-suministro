package com.logisync.sistema_cadena_suministro.domain.orders.chain;

/**
 * Chain of Responsibility: Abstract validator for order creation.
 */
public abstract class OrderCreationValidator {

    private OrderCreationValidator next;

    public OrderCreationValidator linkWith(OrderCreationValidator next) {
        this.next = next;
        return next;
    }

    public void validate(OrderCreationContext context) {
        check(context);
        if (next != null) {
            next.validate(context);
        }
    }

    protected abstract void check(OrderCreationContext context);
}

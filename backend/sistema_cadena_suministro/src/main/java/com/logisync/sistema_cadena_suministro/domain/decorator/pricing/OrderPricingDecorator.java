package com.logisync.sistema_cadena_suministro.domain.decorator.pricing;

import java.math.BigDecimal;

/**
 * Decorator: Base decorator maintaining a reference to a component.
 */
public abstract class OrderPricingDecorator implements OrderPricingCalculator {

    protected final OrderPricingCalculator wrappee;

    protected OrderPricingDecorator(OrderPricingCalculator wrappee) {
        this.wrappee = wrappee;
    }

    @Override
    public BigDecimal calculateFinalPrice(BigDecimal baseAmount, int totalUnits) {
        return wrappee.calculateFinalPrice(baseAmount, totalUnits);
    }
}

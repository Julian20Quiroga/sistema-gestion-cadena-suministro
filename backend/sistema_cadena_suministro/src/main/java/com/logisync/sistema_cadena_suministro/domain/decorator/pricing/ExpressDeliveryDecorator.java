package com.logisync.sistema_cadena_suministro.domain.decorator.pricing;

import java.math.BigDecimal;

/**
 * Decorator: Adds an express delivery flat fee.
 */
public class ExpressDeliveryDecorator extends OrderPricingDecorator {

    private final BigDecimal expressFee;

    public ExpressDeliveryDecorator(OrderPricingCalculator wrappee, BigDecimal expressFee) {
        super(wrappee);
        this.expressFee = expressFee;
    }

    @Override
    public BigDecimal calculateFinalPrice(BigDecimal baseAmount, int totalUnits) {
        BigDecimal current = super.calculateFinalPrice(baseAmount, totalUnits);
        return current.add(expressFee);
    }
}

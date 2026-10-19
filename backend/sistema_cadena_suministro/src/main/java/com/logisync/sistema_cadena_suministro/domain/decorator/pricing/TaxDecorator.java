package com.logisync.sistema_cadena_suministro.domain.decorator.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Decorator: Applies VAT / sales tax to the total price.
 */
public class TaxDecorator extends OrderPricingDecorator {

    private final BigDecimal taxRate;

    public TaxDecorator(OrderPricingCalculator wrappee, BigDecimal taxRate) {
        super(wrappee);
        this.taxRate = taxRate;
    }

    @Override
    public BigDecimal calculateFinalPrice(BigDecimal baseAmount, int totalUnits) {
        BigDecimal current = super.calculateFinalPrice(baseAmount, totalUnits);
        BigDecimal tax = current.multiply(taxRate);
        return current.add(tax).setScale(2, RoundingMode.HALF_UP);
    }
}

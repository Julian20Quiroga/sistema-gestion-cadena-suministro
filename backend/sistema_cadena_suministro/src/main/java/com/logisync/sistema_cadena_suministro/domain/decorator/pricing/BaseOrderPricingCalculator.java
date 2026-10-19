package com.logisync.sistema_cadena_suministro.domain.decorator.pricing;

import java.math.BigDecimal;

/**
 * Decorator: Concrete component providing standard base pricing.
 */
public class BaseOrderPricingCalculator implements OrderPricingCalculator {

    @Override
    public BigDecimal calculateFinalPrice(BigDecimal baseAmount, int totalUnits) {
        return baseAmount != null ? baseAmount : BigDecimal.ZERO;
    }
}

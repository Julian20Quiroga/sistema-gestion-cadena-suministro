package com.logisync.sistema_cadena_suministro.domain.decorator.pricing;

import java.math.BigDecimal;

/**
 * Decorator: Component interface for calculating dynamic order pricing.
 */
public interface OrderPricingCalculator {
    BigDecimal calculateFinalPrice(BigDecimal baseAmount, int totalUnits);
}

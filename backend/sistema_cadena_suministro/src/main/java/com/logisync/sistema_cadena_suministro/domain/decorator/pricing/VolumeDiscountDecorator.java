package com.logisync.sistema_cadena_suministro.domain.decorator.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Decorator: Applies a volume discount percentage when unit threshold is met.
 */
public class VolumeDiscountDecorator extends OrderPricingDecorator {

    private final int thresholdUnits;
    private final BigDecimal discountRate;

    public VolumeDiscountDecorator(OrderPricingCalculator wrappee, int thresholdUnits, BigDecimal discountRate) {
        super(wrappee);
        this.thresholdUnits = thresholdUnits;
        this.discountRate = discountRate;
    }

    @Override
    public BigDecimal calculateFinalPrice(BigDecimal baseAmount, int totalUnits) {
        BigDecimal price = super.calculateFinalPrice(baseAmount, totalUnits);
        if (totalUnits >= thresholdUnits) {
            BigDecimal discount = price.multiply(discountRate);
            return price.subtract(discount).setScale(2, RoundingMode.HALF_UP);
        }
        return price;
    }
}

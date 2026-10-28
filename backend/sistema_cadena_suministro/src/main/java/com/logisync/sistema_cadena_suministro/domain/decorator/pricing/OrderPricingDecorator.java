package com.logisync.sistema_cadena_suministro.domain.decorator.pricing;

import java.math.BigDecimal;

/**
 * Clase base abstracta para los decoradores de cálculo de precio de órdenes.
 * Patrón aplicado: Decorator (mantiene la referencia al calculador envuelto y delega por defecto).
 */
public abstract class OrderPricingDecorator implements OrderPricingCalculator {

    protected final OrderPricingCalculator wrappee;

    public OrderPricingDecorator(OrderPricingCalculator wrappee) {
        this.wrappee = wrappee;
    }

    /**
     * Delega el cálculo de precio al calculador envuelto.
     */
    @Override
    public BigDecimal calculateFinalPrice(BigDecimal baseAmount, int totalUnits) {
        return wrappee.calculateFinalPrice(baseAmount, totalUnits);
    }
}

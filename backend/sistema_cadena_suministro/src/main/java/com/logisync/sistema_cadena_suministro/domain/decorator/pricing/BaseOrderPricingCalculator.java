package com.logisync.sistema_cadena_suministro.domain.decorator.pricing;

import java.math.BigDecimal;

/**
 * Componente base concreto para el cálculo de tarifas de órdenes.
 * Patrón aplicado: Decorator (Componente Concreto que entrega el precio base sin recargos ni descuentos).
 */
public class BaseOrderPricingCalculator implements OrderPricingCalculator {

    /**
     * Retorna el precio base sin modificaciones adicionales.
     */
    @Override
    public BigDecimal calculateFinalPrice(BigDecimal baseAmount, int totalUnits) {
        return baseAmount != null ? baseAmount : BigDecimal.ZERO;
    }
}

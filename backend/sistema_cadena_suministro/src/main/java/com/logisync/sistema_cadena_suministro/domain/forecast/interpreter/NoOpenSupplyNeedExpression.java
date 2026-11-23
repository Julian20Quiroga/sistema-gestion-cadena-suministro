package com.logisync.sistema_cadena_suministro.domain.forecast.interpreter;

/**
 * Expresión terminal: no debe existir ya una necesidad abierta para el producto.
 */
public final class NoOpenSupplyNeedExpression implements SupplyNeedExpression {
    @Override
    public boolean interpret(SupplyNeedRuleContext context) {
        return !context.openNeedExists();
    }
}
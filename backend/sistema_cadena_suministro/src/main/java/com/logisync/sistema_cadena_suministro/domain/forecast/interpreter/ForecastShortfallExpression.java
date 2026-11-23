package com.logisync.sistema_cadena_suministro.domain.forecast.interpreter;

/**
 * Expresión terminal: la proyección debe indicar unidades faltantes.
 */
public final class ForecastShortfallExpression implements SupplyNeedExpression {
    @Override
    public boolean interpret(SupplyNeedRuleContext context) {
        return context.shortage() > 0;
    }
}
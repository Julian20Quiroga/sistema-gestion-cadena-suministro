package com.logisync.sistema_cadena_suministro.domain.forecast.interpreter;

/**
 * Expresión terminal: la creación automática debe estar habilitada.
 */
public final class AutoCreationEnabledExpression implements SupplyNeedExpression {
    @Override
    public boolean interpret(SupplyNeedRuleContext context) {
        return context.automaticCreationEnabled();
    }
}
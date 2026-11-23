package com.logisync.sistema_cadena_suministro.domain.forecast.interpreter;

import java.util.Objects;

/**
 * Expresión no terminal que interpreta la conjunción lógica de dos reglas.
 */
public record AndSupplyNeedExpression(
        SupplyNeedExpression left,
        SupplyNeedExpression right) implements SupplyNeedExpression {

    public AndSupplyNeedExpression {
        Objects.requireNonNull(left, "left expression is required");
        Objects.requireNonNull(right, "right expression is required");
    }

    @Override
    public boolean interpret(SupplyNeedRuleContext context) {
        return left.interpret(context) && right.interpret(context);
    }
}
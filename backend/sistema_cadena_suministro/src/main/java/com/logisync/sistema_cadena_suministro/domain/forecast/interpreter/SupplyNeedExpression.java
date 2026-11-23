package com.logisync.sistema_cadena_suministro.domain.forecast.interpreter;

/**
 * Expresión de la gramática de reglas booleanas de abastecimiento.
 */
@FunctionalInterface
public interface SupplyNeedExpression {
    boolean interpret(SupplyNeedRuleContext context);
}
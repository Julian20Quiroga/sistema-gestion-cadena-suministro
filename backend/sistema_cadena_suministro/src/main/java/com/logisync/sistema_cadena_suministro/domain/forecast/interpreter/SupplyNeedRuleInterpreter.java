package com.logisync.sistema_cadena_suministro.domain.forecast.interpreter;

/**
 * Interpreta la regla de negocio:
 * AUTO_CREATION_ENABLED AND FORECAST_SHORTFALL AND NO_OPEN_SUPPLY_NEED.
 *
 * La expresión se representa como un árbol: los nodos terminales interpretan
 * cada condición y los nodos no terminales combinan sus resultados.
 */
public final class SupplyNeedRuleInterpreter {
    private final SupplyNeedExpression rule = new AndSupplyNeedExpression(
            new AutoCreationEnabledExpression(),
            new AndSupplyNeedExpression(
                    new ForecastShortfallExpression(),
                    new NoOpenSupplyNeedExpression()));

    public boolean shouldCreate(SupplyNeedRuleContext context) {
        return rule.interpret(context);
    }
}
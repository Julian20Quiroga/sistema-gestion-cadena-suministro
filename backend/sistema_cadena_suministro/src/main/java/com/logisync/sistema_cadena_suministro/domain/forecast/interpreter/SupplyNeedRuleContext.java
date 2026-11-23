package com.logisync.sistema_cadena_suministro.domain.forecast.interpreter;

/**
 * Contexto que aporta los datos necesarios para evaluar una regla de creación
 * automática de necesidades de abastecimiento.
 */
public record SupplyNeedRuleContext(
        boolean automaticCreationEnabled,
        int shortage,
        boolean openNeedExists) {
}
package com.logisync.sistema_cadena_suministro.domain.procurement.chain;

import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;

/**
 * Chain of Responsibility: Validates destination warehouse exists and is active.
 */
public class WarehouseActiveValidator extends CreationOrderValidator {

    @Override
    protected void check(CreationValidationContext context) {
        if (context.warehouse() == null || !context.warehouse().isActive()) {
            throw new BusinessRuleException("Warehouse is missing or inactive");
        }
    }
}

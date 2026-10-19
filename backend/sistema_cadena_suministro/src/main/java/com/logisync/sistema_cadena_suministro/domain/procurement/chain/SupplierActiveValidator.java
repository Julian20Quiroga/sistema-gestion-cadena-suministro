package com.logisync.sistema_cadena_suministro.domain.procurement.chain;

import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;

/**
 * Chain of Responsibility: Validates supplier exists and is active.
 */
public class SupplierActiveValidator extends CreationOrderValidator {

    @Override
    protected void check(CreationValidationContext context) {
        if (context.supplier() == null || !context.supplier().isActive()) {
            throw new BusinessRuleException("Supplier is missing or inactive");
        }
    }
}

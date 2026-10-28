package com.logisync.sistema_cadena_suministro.domain.procurement.chain;

/**
 * Chain of Responsibility: Validator for purchase order creation prerequisites.
 */
public abstract class CreationOrderValidator {

    private CreationOrderValidator next;

    public CreationOrderValidator linkWith(CreationOrderValidator next) {
        this.next = next;
        return next;
    }

    public void validate(CreationValidationContext context) {
        check(context);
        if (next != null) {
            next.validate(context);
        }
    }

    protected abstract void check(CreationValidationContext context);
}

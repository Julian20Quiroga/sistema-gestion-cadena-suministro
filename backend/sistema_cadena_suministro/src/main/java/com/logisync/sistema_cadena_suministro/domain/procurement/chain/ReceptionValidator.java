package com.logisync.sistema_cadena_suministro.domain.procurement.chain;

/**
 * Chain of Responsibility: Abstract step for validating purchase order merchandise reception.
 */
public abstract class ReceptionValidator {

    private ReceptionValidator next;

    public ReceptionValidator linkWith(ReceptionValidator next) {
        this.next = next;
        return next;
    }

    public void validate(ReceptionValidationContext context) {
        check(context);
        if (next != null) {
            next.validate(context);
        }
    }

    protected abstract void check(ReceptionValidationContext context);
}

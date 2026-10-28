package com.logisync.sistema_cadena_suministro.domain.procurement.chain;

import com.logisync.sistema_cadena_suministro.domain.enums.PurchaseOrderStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.InvalidStateTransitionException;

/**
 * Chain of Responsibility: Validates order is eligible to receive merchandise.
 */
public class ReceptionOrderStateValidator extends ReceptionValidator {

    @Override
    protected void check(ReceptionValidationContext context) {
        PurchaseOrderStatus status = context.order().getStatus();
        if (status == PurchaseOrderStatus.ANULADA) {
            throw new InvalidStateTransitionException("PurchaseOrder", status.name(), "receive items");
        }
        if (status == PurchaseOrderStatus.RECIBIDA) {
            throw new InvalidStateTransitionException("PurchaseOrder", status.name(), "receive items");
        }
    }
}

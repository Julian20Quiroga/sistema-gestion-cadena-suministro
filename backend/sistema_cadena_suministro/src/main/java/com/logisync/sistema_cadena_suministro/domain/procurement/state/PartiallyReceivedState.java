package com.logisync.sistema_cadena_suministro.domain.procurement.state;

import com.logisync.sistema_cadena_suministro.domain.enums.PurchaseOrderStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.InvalidStateTransitionException;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrder;

/**
 * State: Concrete state for partially received purchase orders.
 */
public class PartiallyReceivedState implements PurchaseOrderState {

    @Override
    public PurchaseOrderStatus getStatus() {
        return PurchaseOrderStatus.RECIBIDA_PARCIAL;
    }

    @Override
    public void receive(PurchaseOrder order, int totalReceivedSoFar, int totalOrdered) {
        if (totalReceivedSoFar >= totalOrdered) {
            order.changeState(new ReceivedState());
        }
    }

    @Override
    public void cancel(PurchaseOrder order) {
        throw new InvalidStateTransitionException("PurchaseOrder", getStatus().name(), "cancel");
    }
}

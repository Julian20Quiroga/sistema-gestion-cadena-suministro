package com.logisync.sistema_cadena_suministro.domain.procurement.state;

import com.logisync.sistema_cadena_suministro.domain.enums.PurchaseOrderStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.InvalidStateTransitionException;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrder;

/**
 * State: Concrete state for cancelled purchase orders.
 */
public class CancelledState implements PurchaseOrderState {

    @Override
    public PurchaseOrderStatus getStatus() {
        return PurchaseOrderStatus.ANULADA;
    }

    @Override
    public void receive(PurchaseOrder order, int totalReceivedSoFar, int totalOrdered) {
        throw new InvalidStateTransitionException("PurchaseOrder", getStatus().name(), "receive items");
    }

    @Override
    public void cancel(PurchaseOrder order) {
        throw new InvalidStateTransitionException("PurchaseOrder", getStatus().name(), "cancel");
    }
}

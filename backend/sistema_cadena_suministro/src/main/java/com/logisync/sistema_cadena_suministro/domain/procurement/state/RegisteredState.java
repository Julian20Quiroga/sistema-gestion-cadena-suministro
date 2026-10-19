package com.logisync.sistema_cadena_suministro.domain.procurement.state;

import com.logisync.sistema_cadena_suministro.domain.enums.PurchaseOrderStatus;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrder;

/**
 * State: Concrete state for newly registered purchase orders.
 */
public class RegisteredState implements PurchaseOrderState {

    @Override
    public PurchaseOrderStatus getStatus() {
        return PurchaseOrderStatus.REGISTRADA;
    }

    @Override
    public void receive(PurchaseOrder order, int totalReceivedSoFar, int totalOrdered) {
        if (totalReceivedSoFar >= totalOrdered) {
            order.changeState(new ReceivedState());
        } else if (totalReceivedSoFar > 0) {
            order.changeState(new PartiallyReceivedState());
        }
    }

    @Override
    public void cancel(PurchaseOrder order) {
        order.changeState(new CancelledState());
    }
}

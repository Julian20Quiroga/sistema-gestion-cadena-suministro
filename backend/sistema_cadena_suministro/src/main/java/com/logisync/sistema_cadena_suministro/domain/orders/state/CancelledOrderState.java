package com.logisync.sistema_cadena_suministro.domain.orders.state;

import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.InvalidStateTransitionException;
import com.logisync.sistema_cadena_suministro.domain.models.Order;

/**
 * State: Concrete terminal state for cancelled orders.
 */
public class CancelledOrderState implements OrderState {

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.ANULADO;
    }

    @Override
    public void startPreparation(Order order) {
        throw new InvalidStateTransitionException("Order", getStatus().name(), "start preparation for cancelled order");
    }

    @Override
    public void markDispatched(Order order) {
        throw new InvalidStateTransitionException("Order", getStatus().name(), "dispatch a cancelled order");
    }

    @Override
    public void markDelivered(Order order) {
        throw new InvalidStateTransitionException("Order", getStatus().name(), "deliver a cancelled order");
    }

    @Override
    public void cancel(Order order) {
        throw new InvalidStateTransitionException("Order", getStatus().name(), "cancel an already cancelled order");
    }
}

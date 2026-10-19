package com.logisync.sistema_cadena_suministro.domain.orders.state;

import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.InvalidStateTransitionException;
import com.logisync.sistema_cadena_suministro.domain.models.Order;

/**
 * State: Concrete terminal state for delivered orders.
 */
public class DeliveredOrderState implements OrderState {

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.ENTREGADO;
    }

    @Override
    public void startPreparation(Order order) {
        throw new InvalidStateTransitionException("Order", getStatus().name(), "start preparation for delivered order");
    }

    @Override
    public void markDispatched(Order order) {
        throw new InvalidStateTransitionException("Order", getStatus().name(), "dispatch a delivered order");
    }

    @Override
    public void markDelivered(Order order) {
        throw new InvalidStateTransitionException("Order", getStatus().name(), "deliver again");
    }

    @Override
    public void cancel(Order order) {
        throw new InvalidStateTransitionException("Order", getStatus().name(), "cancel a delivered order");
    }
}

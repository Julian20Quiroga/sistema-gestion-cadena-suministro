package com.logisync.sistema_cadena_suministro.domain.orders.state;

import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.InvalidStateTransitionException;
import com.logisync.sistema_cadena_suministro.domain.models.Order;

/**
 * State: Concrete state for orders out for delivery.
 */
public class DispatchedOrderState implements OrderState {

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.DESPACHADO;
    }

    @Override
    public void startPreparation(Order order) {
        throw new InvalidStateTransitionException("Order", getStatus().name(), "start preparation when already dispatched");
    }

    @Override
    public void markDispatched(Order order) {
        throw new InvalidStateTransitionException("Order", getStatus().name(), "dispatch again");
    }

    @Override
    public void markDelivered(Order order) {
        order.changeState(new DeliveredOrderState());
    }

    @Override
    public void cancel(Order order) {
        throw new InvalidStateTransitionException("Order", getStatus().name(), "cancel an in-transit order");
    }
}

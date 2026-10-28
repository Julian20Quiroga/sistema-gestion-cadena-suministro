package com.logisync.sistema_cadena_suministro.domain.orders.state;

import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.InvalidStateTransitionException;
import com.logisync.sistema_cadena_suministro.domain.models.Order;

/**
 * State: Concrete state for newly registered orders.
 */
public class RegisteredOrderState implements OrderState {

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.REGISTRADO;
    }

    @Override
    public void startPreparation(Order order) {
        order.changeState(new PreparingOrderState());
    }

    @Override
    public void markDispatched(Order order) {
        throw new InvalidStateTransitionException("Order", getStatus().name(), "dispatch directly without preparation");
    }

    @Override
    public void markDelivered(Order order) {
        throw new InvalidStateTransitionException("Order", getStatus().name(), "deliver without being dispatched");
    }

    @Override
    public void cancel(Order order) {
        order.changeState(new CancelledOrderState());
    }
}

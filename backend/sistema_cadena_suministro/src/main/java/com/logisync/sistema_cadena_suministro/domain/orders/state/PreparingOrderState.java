package com.logisync.sistema_cadena_suministro.domain.orders.state;

import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.InvalidStateTransitionException;
import com.logisync.sistema_cadena_suministro.domain.models.Order;

/**
 * State: Concrete state for orders currently being picked and packed.
 */
public class PreparingOrderState implements OrderState {

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.PREPARACION;
    }

    @Override
    public void startPreparation(Order order) {
        throw new InvalidStateTransitionException("Order", getStatus().name(), "start preparation again");
    }

    @Override
    public void markDispatched(Order order) {
        order.changeState(new DispatchedOrderState());
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

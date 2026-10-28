package com.logisync.sistema_cadena_suministro.domain.orders.state;

import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;

public final class OrderStateFactory {

    private OrderStateFactory() {}

    public static OrderState from(OrderStatus status) {
        if (status == null) {
            return new RegisteredOrderState();
        }
        return switch (status) {
            case REGISTRADO -> new RegisteredOrderState();
            case PREPARACION -> new PreparingOrderState();
            case DESPACHADO -> new DispatchedOrderState();
            case ENTREGADO -> new DeliveredOrderState();
            case ANULADO -> new CancelledOrderState();
        };
    }
}

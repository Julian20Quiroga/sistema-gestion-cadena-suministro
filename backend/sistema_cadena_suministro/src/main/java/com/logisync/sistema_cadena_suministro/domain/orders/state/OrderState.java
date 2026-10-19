package com.logisync.sistema_cadena_suministro.domain.orders.state;

import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;
import com.logisync.sistema_cadena_suministro.domain.models.Order;

/**
 * State: Encapsulates state-specific behavior and transitions for customer orders.
 */
public interface OrderState {
    OrderStatus getStatus();
    void startPreparation(Order order);
    void markDispatched(Order order);
    void markDelivered(Order order);
    void cancel(Order order);
}

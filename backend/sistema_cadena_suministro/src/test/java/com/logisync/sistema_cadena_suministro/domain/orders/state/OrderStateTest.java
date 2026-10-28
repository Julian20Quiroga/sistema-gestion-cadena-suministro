package com.logisync.sistema_cadena_suministro.domain.orders.state;

import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.InvalidStateTransitionException;
import com.logisync.sistema_cadena_suministro.domain.models.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderStateTest {

    @Test
    void standardLifecycleFromRegistrationToDelivery() {
        Order order = new Order();
        assertEquals(OrderStatus.REGISTRADO, order.getStatus());

        order.startPreparation();
        assertEquals(OrderStatus.PREPARACION, order.getStatus());

        order.markDispatched();
        assertEquals(OrderStatus.DESPACHADO, order.getStatus());

        order.markDelivered();
        assertEquals(OrderStatus.ENTREGADO, order.getStatus());

        assertThrows(InvalidStateTransitionException.class, order::startPreparation);
        assertThrows(InvalidStateTransitionException.class, order::cancel);
    }

    @Test
    void cannotDispatchDirectlyFromRegistered() {
        Order order = new Order();
        assertThrows(InvalidStateTransitionException.class, order::markDispatched);
    }

    @Test
    void canCancelFromRegisteredOrPreparation() {
        Order o1 = new Order();
        o1.cancel();
        assertEquals(OrderStatus.ANULADO, o1.getStatus());

        Order o2 = new Order();
        o2.startPreparation();
        o2.cancel();
        assertEquals(OrderStatus.ANULADO, o2.getStatus());
    }

    @Test
    void cannotCancelWhenDispatched() {
        Order order = new Order();
        order.startPreparation();
        order.markDispatched();
        assertThrows(InvalidStateTransitionException.class, order::cancel);
    }
}

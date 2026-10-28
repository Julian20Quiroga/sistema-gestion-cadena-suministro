package com.logisync.sistema_cadena_suministro.domain.dispatch.state;

import com.logisync.sistema_cadena_suministro.domain.enums.DispatchStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.InvalidStateTransitionException;
import com.logisync.sistema_cadena_suministro.domain.models.Dispatch;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DispatchStateTest {

    @Test
    void standardDispatchLifecycleFromPendingToDelivered() {
        Dispatch dispatch = new Dispatch();
        assertEquals(DispatchStatus.PENDIENTE, dispatch.getStatus());

        dispatch.startPreparation();
        assertEquals(DispatchStatus.PREPARANDO, dispatch.getStatus());

        dispatch.startTransport();
        assertEquals(DispatchStatus.EN_TRANSPORTE, dispatch.getStatus());

        dispatch.markDelivered();
        assertEquals(DispatchStatus.ENTREGADO, dispatch.getStatus());

        assertThrows(InvalidStateTransitionException.class, dispatch::startPreparation);
        assertThrows(InvalidStateTransitionException.class, dispatch::cancel);
    }

    @Test
    void cannotShipWithoutPreparation() {
        Dispatch dispatch = new Dispatch();
        assertThrows(InvalidStateTransitionException.class, dispatch::startTransport);
    }

    @Test
    void canCancelFromPendingOrPreparing() {
        Dispatch d1 = new Dispatch();
        d1.cancel();
        assertEquals(DispatchStatus.CANCELADO, d1.getStatus());

        Dispatch d2 = new Dispatch();
        d2.startPreparation();
        d2.cancel();
        assertEquals(DispatchStatus.CANCELADO, d2.getStatus());
    }

    @Test
    void cannotCancelWhenInTransit() {
        Dispatch dispatch = new Dispatch();
        dispatch.startPreparation();
        dispatch.startTransport();
        assertThrows(InvalidStateTransitionException.class, dispatch::cancel);
    }
}

package com.logisync.sistema_cadena_suministro.domain.procurement.state;

import com.logisync.sistema_cadena_suministro.domain.enums.PurchaseOrderStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.InvalidStateTransitionException;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PurchaseOrderStateTest {

    @Test
    void transitionsFromRegisteredToPartiallyReceivedThenReceived() {
        PurchaseOrder order = new PurchaseOrder();
        assertEquals(PurchaseOrderStatus.REGISTRADA, order.getStatus());

        order.applyReception(5, 10);
        assertEquals(PurchaseOrderStatus.RECIBIDA_PARCIAL, order.getStatus());

        order.applyReception(10, 10);
        assertEquals(PurchaseOrderStatus.RECIBIDA, order.getStatus());

        assertThrows(InvalidStateTransitionException.class, () -> order.applyReception(11, 10));
        assertThrows(InvalidStateTransitionException.class, order::cancel);
    }

    @Test
    void allowsCancellingOnlyFromRegistered() {
        PurchaseOrder order = new PurchaseOrder();
        order.cancel();
        assertEquals(PurchaseOrderStatus.ANULADA, order.getStatus());

        assertThrows(InvalidStateTransitionException.class, order::cancel);
        assertThrows(InvalidStateTransitionException.class, () -> order.applyReception(1, 1));
    }

    @Test
    void cannotCancelPartiallyReceivedOrder() {
        PurchaseOrder order = new PurchaseOrder();
        order.applyReception(3, 10);
        assertThrows(InvalidStateTransitionException.class, order::cancel);
    }
}

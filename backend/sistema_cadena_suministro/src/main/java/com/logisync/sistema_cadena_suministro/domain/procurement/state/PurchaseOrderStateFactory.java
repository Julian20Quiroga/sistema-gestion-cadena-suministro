package com.logisync.sistema_cadena_suministro.domain.procurement.state;

import com.logisync.sistema_cadena_suministro.domain.enums.PurchaseOrderStatus;

public final class PurchaseOrderStateFactory {

    private PurchaseOrderStateFactory() {}

    public static PurchaseOrderState from(PurchaseOrderStatus status) {
        if (status == null) {
            return new RegisteredState();
        }
        return switch (status) {
            case REGISTRADA -> new RegisteredState();
            case RECIBIDA_PARCIAL -> new PartiallyReceivedState();
            case RECIBIDA -> new ReceivedState();
            case ANULADA -> new CancelledState();
        };
    }
}

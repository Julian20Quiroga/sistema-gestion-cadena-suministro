package com.logisync.sistema_cadena_suministro.domain.dispatch.state;

import com.logisync.sistema_cadena_suministro.domain.enums.DispatchStatus;

public final class DispatchStateFactory {

    private DispatchStateFactory() {}

    public static DispatchState from(DispatchStatus status) {
        if (status == null) {
            return new PendingDispatchState();
        }
        return switch (status) {
            case PENDIENTE -> new PendingDispatchState();
            case PREPARANDO -> new PreparingDispatchState();
            case EN_TRANSPORTE -> new InTransitDispatchState();
            case ENTREGADO -> new DeliveredDispatchState();
            case CANCELADO -> new CancelledDispatchState();
        };
    }
}

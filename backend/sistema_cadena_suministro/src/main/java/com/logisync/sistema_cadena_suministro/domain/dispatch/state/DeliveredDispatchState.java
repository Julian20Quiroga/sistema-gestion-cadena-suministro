package com.logisync.sistema_cadena_suministro.domain.dispatch.state;

import com.logisync.sistema_cadena_suministro.domain.enums.DispatchStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.InvalidStateTransitionException;
import com.logisync.sistema_cadena_suministro.domain.models.Dispatch;

/**
 * State: Concrete terminal state for fulfilled dispatches.
 */
public class DeliveredDispatchState implements DispatchState {

    @Override
    public DispatchStatus getStatus() {
        return DispatchStatus.ENTREGADO;
    }

    @Override
    public void startPreparation(Dispatch dispatch) {
        throw new InvalidStateTransitionException("Dispatch", getStatus().name(), "start preparation for delivered dispatch");
    }

    @Override
    public void startTransport(Dispatch dispatch) {
        throw new InvalidStateTransitionException("Dispatch", getStatus().name(), "start transport for delivered dispatch");
    }

    @Override
    public void markDelivered(Dispatch dispatch) {
        throw new InvalidStateTransitionException("Dispatch", getStatus().name(), "deliver an already delivered dispatch");
    }

    @Override
    public void cancel(Dispatch dispatch) {
        throw new InvalidStateTransitionException("Dispatch", getStatus().name(), "cancel a delivered dispatch");
    }
}

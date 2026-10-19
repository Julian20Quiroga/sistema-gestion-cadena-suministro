package com.logisync.sistema_cadena_suministro.domain.dispatch.state;

import com.logisync.sistema_cadena_suministro.domain.enums.DispatchStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.InvalidStateTransitionException;
import com.logisync.sistema_cadena_suministro.domain.models.Dispatch;

/**
 * State: Concrete terminal state for cancelled dispatches.
 */
public class CancelledDispatchState implements DispatchState {

    @Override
    public DispatchStatus getStatus() {
        return DispatchStatus.CANCELADO;
    }

    @Override
    public void startPreparation(Dispatch dispatch) {
        throw new InvalidStateTransitionException("Dispatch", getStatus().name(), "start preparation for cancelled dispatch");
    }

    @Override
    public void startTransport(Dispatch dispatch) {
        throw new InvalidStateTransitionException("Dispatch", getStatus().name(), "start transport for cancelled dispatch");
    }

    @Override
    public void markDelivered(Dispatch dispatch) {
        throw new InvalidStateTransitionException("Dispatch", getStatus().name(), "deliver a cancelled dispatch");
    }

    @Override
    public void cancel(Dispatch dispatch) {
        throw new InvalidStateTransitionException("Dispatch", getStatus().name(), "cancel an already cancelled dispatch");
    }
}

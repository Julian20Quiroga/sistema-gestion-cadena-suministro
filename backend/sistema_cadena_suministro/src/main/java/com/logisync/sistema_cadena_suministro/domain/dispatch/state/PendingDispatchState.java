package com.logisync.sistema_cadena_suministro.domain.dispatch.state;

import com.logisync.sistema_cadena_suministro.domain.enums.DispatchStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.InvalidStateTransitionException;
import com.logisync.sistema_cadena_suministro.domain.models.Dispatch;

/**
 * State: Concrete state for newly created pending dispatches.
 */
public class PendingDispatchState implements DispatchState {

    @Override
    public DispatchStatus getStatus() {
        return DispatchStatus.PENDIENTE;
    }

    @Override
    public void startPreparation(Dispatch dispatch) {
        dispatch.changeState(new PreparingDispatchState());
    }

    @Override
    public void startTransport(Dispatch dispatch) {
        throw new InvalidStateTransitionException("Dispatch", getStatus().name(), "start transport before preparation");
    }

    @Override
    public void markDelivered(Dispatch dispatch) {
        throw new InvalidStateTransitionException("Dispatch", getStatus().name(), "deliver before being shipped");
    }

    @Override
    public void cancel(Dispatch dispatch) {
        dispatch.changeState(new CancelledDispatchState());
    }
}

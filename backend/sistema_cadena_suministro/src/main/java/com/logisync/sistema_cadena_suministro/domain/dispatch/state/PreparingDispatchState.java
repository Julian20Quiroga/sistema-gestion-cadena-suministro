package com.logisync.sistema_cadena_suministro.domain.dispatch.state;

import com.logisync.sistema_cadena_suministro.domain.enums.DispatchStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.InvalidStateTransitionException;
import com.logisync.sistema_cadena_suministro.domain.models.Dispatch;

/**
 * State: Concrete state for dispatches undergoing warehouse picking and packing.
 */
public class PreparingDispatchState implements DispatchState {

    @Override
    public DispatchStatus getStatus() {
        return DispatchStatus.PREPARANDO;
    }

    @Override
    public void startPreparation(Dispatch dispatch) {
        throw new InvalidStateTransitionException("Dispatch", getStatus().name(), "start preparation again");
    }

    @Override
    public void startTransport(Dispatch dispatch) {
        dispatch.changeState(new InTransitDispatchState());
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

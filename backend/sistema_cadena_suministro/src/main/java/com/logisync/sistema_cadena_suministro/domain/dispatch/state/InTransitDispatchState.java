package com.logisync.sistema_cadena_suministro.domain.dispatch.state;

import com.logisync.sistema_cadena_suministro.domain.enums.DispatchStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.InvalidStateTransitionException;
import com.logisync.sistema_cadena_suministro.domain.models.Dispatch;

/**
 * State: Concrete state for dispatches on the road for delivery.
 */
public class InTransitDispatchState implements DispatchState {

    @Override
    public DispatchStatus getStatus() {
        return DispatchStatus.EN_TRANSPORTE;
    }

    @Override
    public void startPreparation(Dispatch dispatch) {
        throw new InvalidStateTransitionException("Dispatch", getStatus().name(), "start preparation when in transit");
    }

    @Override
    public void startTransport(Dispatch dispatch) {
        throw new InvalidStateTransitionException("Dispatch", getStatus().name(), "start transport again");
    }

    @Override
    public void markDelivered(Dispatch dispatch) {
        dispatch.changeState(new DeliveredDispatchState());
    }

    @Override
    public void cancel(Dispatch dispatch) {
        throw new InvalidStateTransitionException("Dispatch", getStatus().name(), "cancel an in-transit dispatch");
    }
}

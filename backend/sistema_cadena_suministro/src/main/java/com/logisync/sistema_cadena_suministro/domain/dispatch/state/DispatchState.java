package com.logisync.sistema_cadena_suministro.domain.dispatch.state;

import com.logisync.sistema_cadena_suministro.domain.enums.DispatchStatus;
import com.logisync.sistema_cadena_suministro.domain.models.Dispatch;

/**
 * State: Encapsulates state-specific behavior for warehouse dispatches.
 */
public interface DispatchState {
    DispatchStatus getStatus();
    void startPreparation(Dispatch dispatch);
    void startTransport(Dispatch dispatch);
    void markDelivered(Dispatch dispatch);
    void cancel(Dispatch dispatch);
}

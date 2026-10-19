package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.responses.DispatchAppResponse;

public interface ManageDispatchStatusUseCase {
    DispatchAppResponse startPreparation(Integer dispatchId);
    DispatchAppResponse ship(Integer dispatchId);
    DispatchAppResponse deliver(Integer dispatchId);
    DispatchAppResponse cancel(Integer dispatchId);
}

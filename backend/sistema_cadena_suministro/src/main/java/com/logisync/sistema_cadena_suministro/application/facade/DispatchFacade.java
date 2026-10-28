package com.logisync.sistema_cadena_suministro.application.facade;

import com.logisync.sistema_cadena_suministro.application.commands.CreateDispatchCommand;
import com.logisync.sistema_cadena_suministro.application.responses.DispatchAppResponse;

/**
 * Facade: High-level operations coordinating order reservation, vehicle assignment, and stock fulfillment.
 */
public interface DispatchFacade {
    DispatchAppResponse createDispatchWithStockReservation(CreateDispatchCommand command);
    DispatchAppResponse startPreparation(Integer dispatchId);
    DispatchAppResponse shipAndConsumeStock(Integer dispatchId);
    DispatchAppResponse markDelivered(Integer dispatchId);
    DispatchAppResponse cancelAndReleaseStock(Integer dispatchId);
}

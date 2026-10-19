package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.ConsumeReservationCommand;
import com.logisync.sistema_cadena_suministro.application.responses.MovementAppResponse;

public interface ConsumeReservedStockUseCase {
    MovementAppResponse consume(ConsumeReservationCommand command);
}

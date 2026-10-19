package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.RegisterAdjustmentCommand;
import com.logisync.sistema_cadena_suministro.application.responses.MovementAppResponse;

public interface RegisterAdjustmentUseCase {
    MovementAppResponse registerAdjustment(RegisterAdjustmentCommand command);
}

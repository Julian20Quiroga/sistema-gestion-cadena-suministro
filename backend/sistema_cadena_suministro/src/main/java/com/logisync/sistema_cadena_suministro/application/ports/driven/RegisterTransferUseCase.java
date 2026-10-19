package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.RegisterTransferCommand;
import com.logisync.sistema_cadena_suministro.application.responses.MovementAppResponse;

public interface RegisterTransferUseCase {
    MovementAppResponse registerTransfer(RegisterTransferCommand command);
}

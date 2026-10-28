package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.RegisterExitCommand;
import com.logisync.sistema_cadena_suministro.application.responses.MovementAppResponse;

public interface RegisterExitUseCase {
    MovementAppResponse registerExit(RegisterExitCommand command);
}

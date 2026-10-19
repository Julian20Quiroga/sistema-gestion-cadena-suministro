package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.RegisterEntryCommand;
import com.logisync.sistema_cadena_suministro.application.responses.MovementAppResponse;

public interface RegisterEntryUseCase {
    MovementAppResponse registerEntry(RegisterEntryCommand command);
}

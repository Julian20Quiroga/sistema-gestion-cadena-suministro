package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.CreateDispatchCommand;
import com.logisync.sistema_cadena_suministro.application.responses.DispatchAppResponse;

public interface CreateDispatchUseCase {
    DispatchAppResponse create(CreateDispatchCommand command);
}

package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.CreateWarehouseCommand;
import com.logisync.sistema_cadena_suministro.application.responses.WarehouseAppResponse;

public interface CreateWarehouseUseCase {
    WarehouseAppResponse create(CreateWarehouseCommand command);
}

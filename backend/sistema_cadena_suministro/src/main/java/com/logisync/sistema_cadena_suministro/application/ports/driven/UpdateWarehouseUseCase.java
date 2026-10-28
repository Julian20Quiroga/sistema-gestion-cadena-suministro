package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.UpdateWarehouseCommand;
import com.logisync.sistema_cadena_suministro.application.responses.WarehouseAppResponse;

public interface UpdateWarehouseUseCase {
    WarehouseAppResponse update(UpdateWarehouseCommand command, Integer id);
}

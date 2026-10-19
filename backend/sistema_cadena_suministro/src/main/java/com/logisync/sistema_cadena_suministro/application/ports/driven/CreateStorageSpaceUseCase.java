package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.CreateStorageSpaceCommand;
import com.logisync.sistema_cadena_suministro.application.responses.StorageSpaceAppResponse;

public interface CreateStorageSpaceUseCase {
    StorageSpaceAppResponse create(Integer warehouseId, CreateStorageSpaceCommand command);
}

package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.UpdateStorageSpaceCommand;
import com.logisync.sistema_cadena_suministro.application.responses.StorageSpaceAppResponse;

public interface UpdateStorageSpaceUseCase {
    StorageSpaceAppResponse update(Integer warehouseId, Integer spaceId, UpdateStorageSpaceCommand command);
}

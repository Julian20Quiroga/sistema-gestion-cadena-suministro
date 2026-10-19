package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.responses.FreeLocationAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.StorageNodeAppResponse;

import java.util.List;

public interface QueryStorageStructureUseCase {
    StorageNodeAppResponse structure(Integer warehouseId);

    List<FreeLocationAppResponse> freeLocations(Integer warehouseId, long minFreeUnits);
}

package com.logisync.sistema_cadena_suministro.application.responses;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;

public record StorageSpaceAppResponse(Integer id, String name, Integer warehouseId, Integer capacity,
        SoftDeleteStatus status, Integer parentId, StorageSpaceType type) {
}

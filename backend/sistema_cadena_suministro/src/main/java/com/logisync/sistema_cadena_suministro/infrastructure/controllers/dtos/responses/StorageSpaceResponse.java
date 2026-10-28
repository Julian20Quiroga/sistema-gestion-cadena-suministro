package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;

public record StorageSpaceResponse(Integer id, String name, Integer warehouseId, Integer capacity,
        SoftDeleteStatus status, Integer parentId, StorageSpaceType type) {
}

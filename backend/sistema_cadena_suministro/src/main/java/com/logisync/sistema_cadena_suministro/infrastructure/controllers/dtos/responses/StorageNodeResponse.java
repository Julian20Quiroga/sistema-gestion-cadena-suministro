package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;

import java.util.List;

public record StorageNodeResponse(Integer id, StorageSpaceType type, String name, long capacity, long occupancy,
        double usagePercent, List<StorageNodeResponse> children) {
}

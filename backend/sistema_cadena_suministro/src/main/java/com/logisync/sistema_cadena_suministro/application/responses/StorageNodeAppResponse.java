package com.logisync.sistema_cadena_suministro.application.responses;

import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;

import java.util.List;

public record StorageNodeAppResponse(Integer id, StorageSpaceType type, String name, long capacity, long occupancy,
        double usagePercent, List<StorageNodeAppResponse> children) {
}

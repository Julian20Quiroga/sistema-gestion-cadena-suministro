package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

import java.time.LocalDateTime;

public record LayoutSnapshotResponse(
        Integer id,
        Integer warehouseId,
        String snapshotName,
        LocalDateTime createdAt
) {}

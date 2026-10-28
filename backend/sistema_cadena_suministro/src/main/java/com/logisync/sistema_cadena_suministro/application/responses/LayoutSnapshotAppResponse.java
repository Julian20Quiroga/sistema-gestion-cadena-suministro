package com.logisync.sistema_cadena_suministro.application.responses;

import java.time.LocalDateTime;

public record LayoutSnapshotAppResponse(
        Integer id,
        Integer warehouseId,
        String snapshotName,
        LocalDateTime createdAt
) {}

package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record CreateLayoutSnapshotRequest(
        @NotBlank(message = "Snapshot name is required")
        String snapshotName
) {}

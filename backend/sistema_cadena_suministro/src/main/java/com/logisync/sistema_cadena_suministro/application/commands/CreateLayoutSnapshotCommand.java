package com.logisync.sistema_cadena_suministro.application.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateLayoutSnapshotCommand(
        @NotNull Integer warehouseId,
        @NotBlank String snapshotName
) {}

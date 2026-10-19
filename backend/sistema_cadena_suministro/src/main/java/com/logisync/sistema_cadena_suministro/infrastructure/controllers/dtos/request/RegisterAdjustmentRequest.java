package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterAdjustmentRequest(
        @NotNull Integer storageSpaceId,
        @NotNull Integer productId,
        int delta,
        @NotBlank @Size(max = 255) String reason) {
}

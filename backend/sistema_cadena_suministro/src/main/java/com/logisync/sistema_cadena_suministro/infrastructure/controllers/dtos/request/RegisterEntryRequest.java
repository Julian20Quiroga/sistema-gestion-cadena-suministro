package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RegisterEntryRequest(
        @NotNull Integer storageSpaceId,
        @NotNull Integer productId,
        @Positive int quantity,
        @Size(max = 100) String reference) {
}

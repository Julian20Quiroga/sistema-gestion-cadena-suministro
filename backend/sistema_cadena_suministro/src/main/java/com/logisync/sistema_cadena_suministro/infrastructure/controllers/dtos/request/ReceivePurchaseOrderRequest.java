package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record ReceivePurchaseOrderRequest(
        @NotEmpty @Valid List<Item> items
) {
    public record Item(
            @NotNull Integer productId,
            @NotNull Integer storageSpaceId,
            @Positive int quantity
    ) {}
}

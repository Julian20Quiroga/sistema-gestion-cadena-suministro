package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record CreatePurchaseOrderRequest(
        @NotNull Integer supplierId,
        @NotNull Integer warehouseId,
        @NotEmpty @Valid List<Item> items
) {
    public record Item(
            @NotNull Integer productId,
            @Positive int quantity,
            @NotNull @Positive BigDecimal unitPrice
    ) {}
}

package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record UpdateProductRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull Integer supplierId,
        @NotBlank @Size(max = 50) String sku,
        @Size(max = 500) String description,
        @Size(max = 100) String model,
        @PositiveOrZero Integer ram,
        @PositiveOrZero Integer storage,
        @Size(max = 100) String processor,
        @Size(max = 50) String color,
        @NotNull @DecimalMin("0.0") BigDecimal salePrice,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal weightKg,
        @NotNull @PositiveOrZero Integer reorderPoint,
        SoftDeleteStatus status,
        List<Integer> categoryIds) {
}

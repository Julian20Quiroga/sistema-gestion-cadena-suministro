package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Integer id,
        String name,
        Integer supplierId,
        String sku,
        String description,
        String model,
        Integer ram,
        Integer storage,
        String processor,
        String color,
        BigDecimal salePrice,
        BigDecimal weightKg,
        Integer reorderPoint,
        SoftDeleteStatus status,
        List<CategoryResponse> categories) {
}

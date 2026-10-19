package com.logisync.sistema_cadena_suministro.application.commands;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductCommand(
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
        List<Integer> categoryIds) {
}

package com.logisync.sistema_cadena_suministro.application.queries;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;

public record ProductFilter(String name, String sku, Integer supplierId, Integer categoryId,
        SoftDeleteStatus status) {
}

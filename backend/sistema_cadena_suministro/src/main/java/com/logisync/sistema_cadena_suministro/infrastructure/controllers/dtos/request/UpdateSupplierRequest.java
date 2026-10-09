package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;

public record UpdateSupplierRequest(
        String nit,
        String name,
        String phone,
        String email,
        SoftDeleteStatus status) {
}
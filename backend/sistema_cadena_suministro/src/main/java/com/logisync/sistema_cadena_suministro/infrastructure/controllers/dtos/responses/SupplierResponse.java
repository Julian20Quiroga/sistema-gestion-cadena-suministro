package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;

public record SupplierResponse(
        Integer id,
        String nit,
        String name,
        String phone,
        String email,
        SoftDeleteStatus status
) {}

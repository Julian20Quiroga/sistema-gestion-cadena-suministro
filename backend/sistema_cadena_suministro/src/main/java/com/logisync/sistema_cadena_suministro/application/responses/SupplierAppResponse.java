package com.logisync.sistema_cadena_suministro.application.responses;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;

public record SupplierAppResponse(
        Integer id,
        String nit,
        String name,
        String phone,
        String email,
        SoftDeleteStatus status
) {}

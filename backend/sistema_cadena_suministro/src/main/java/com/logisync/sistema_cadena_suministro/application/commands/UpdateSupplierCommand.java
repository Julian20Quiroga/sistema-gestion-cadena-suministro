package com.logisync.sistema_cadena_suministro.application.commands;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;

public record UpdateSupplierCommand(
        String nit,
        String name,
        String phone,
        String email,
        SoftDeleteStatus status) {
}
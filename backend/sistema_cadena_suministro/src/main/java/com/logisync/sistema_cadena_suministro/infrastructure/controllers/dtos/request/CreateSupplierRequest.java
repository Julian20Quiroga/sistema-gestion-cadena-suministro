package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request;

public record CreateSupplierRequest(
        String nit,
        String name,
        String phone,
        String email
) {}

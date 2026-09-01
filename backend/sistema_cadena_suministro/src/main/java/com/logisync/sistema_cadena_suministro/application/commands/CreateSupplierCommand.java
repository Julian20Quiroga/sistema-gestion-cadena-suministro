package com.logisync.sistema_cadena_suministro.application.commands;

public record CreateSupplierCommand(
        String nit,
        String name,
        String phone,
        String email
) {}

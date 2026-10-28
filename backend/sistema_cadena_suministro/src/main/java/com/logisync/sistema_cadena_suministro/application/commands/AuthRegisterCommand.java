package com.logisync.sistema_cadena_suministro.application.commands;

import com.logisync.sistema_cadena_suministro.domain.enums.UserRole;

public record AuthRegisterCommand(
        String username,
        String password,
        UserRole role) {
}
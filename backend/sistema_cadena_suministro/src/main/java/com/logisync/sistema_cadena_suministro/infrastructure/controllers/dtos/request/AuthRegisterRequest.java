package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request;

import com.logisync.sistema_cadena_suministro.domain.enums.UserRole;

public record AuthRegisterRequest(
    String username,
    String password,
    UserRole role
) {}
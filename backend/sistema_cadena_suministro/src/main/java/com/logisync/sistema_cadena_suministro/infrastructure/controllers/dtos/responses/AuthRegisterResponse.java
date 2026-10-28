package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

import com.logisync.sistema_cadena_suministro.domain.enums.UserRole;

public record AuthRegisterResponse(
    String username,
    UserRole role
) {}
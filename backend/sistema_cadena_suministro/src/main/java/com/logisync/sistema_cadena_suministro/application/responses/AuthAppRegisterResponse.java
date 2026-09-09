package com.logisync.sistema_cadena_suministro.application.responses;

import com.logisync.sistema_cadena_suministro.domain.enums.UserRole;

public record AuthAppRegisterResponse(
    String username,
    UserRole role
) {}
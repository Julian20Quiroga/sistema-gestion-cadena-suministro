package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.enums.UserRole;

public record UserManagementResponse(
        String username,
        UserRole role,
        SoftDeleteStatus status
) {}

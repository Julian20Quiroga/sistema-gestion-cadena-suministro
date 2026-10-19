package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

import com.logisync.sistema_cadena_suministro.domain.inventory.MovementStatus;

import java.time.LocalDateTime;
import java.util.List;

public record MovementResponse(Integer id, String type, LocalDateTime date, String reason, String reference,
        MovementStatus status, List<MovementDetailResponse> details) {
}

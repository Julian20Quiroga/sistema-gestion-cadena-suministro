package com.logisync.sistema_cadena_suministro.application.responses;

import com.logisync.sistema_cadena_suministro.domain.inventory.MovementStatus;

import java.time.LocalDateTime;
import java.util.List;

public record MovementAppResponse(Integer id, String type, LocalDateTime date, String reason, String reference,
        MovementStatus status, List<MovementDetailAppResponse> details) {
}

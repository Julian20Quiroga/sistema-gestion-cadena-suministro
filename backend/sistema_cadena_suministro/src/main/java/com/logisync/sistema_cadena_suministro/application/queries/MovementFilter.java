package com.logisync.sistema_cadena_suministro.application.queries;

import java.time.LocalDateTime;

public record MovementFilter(Integer productId, String type, LocalDateTime from, LocalDateTime to) {
}

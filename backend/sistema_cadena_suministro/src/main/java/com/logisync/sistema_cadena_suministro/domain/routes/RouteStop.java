package com.logisync.sistema_cadena_suministro.domain.routes;

import java.math.BigDecimal;

public record RouteStop(
        int sequence,
        String name,
        BigDecimal latitude,
        BigDecimal longitude,
        Integer orderId,
        double distanceFromPreviousKm
) {}

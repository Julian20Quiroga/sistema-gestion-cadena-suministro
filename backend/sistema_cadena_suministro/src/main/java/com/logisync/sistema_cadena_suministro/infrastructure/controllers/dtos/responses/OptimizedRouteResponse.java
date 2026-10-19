package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

import com.logisync.sistema_cadena_suministro.domain.routes.RouteStop;

import java.math.BigDecimal;
import java.util.List;

public record OptimizedRouteResponse(
        String strategyUsed,
        double totalDistanceKm,
        long estimatedDurationMinutes,
        BigDecimal totalPayloadKg,
        List<RouteStop> stops,
        String geoJsonGeometry
) {}

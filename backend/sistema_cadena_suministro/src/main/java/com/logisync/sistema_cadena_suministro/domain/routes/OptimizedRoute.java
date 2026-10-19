package com.logisync.sistema_cadena_suministro.domain.routes;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

public record OptimizedRoute(
        String strategyUsed,
        double totalDistanceKm,
        long estimatedDurationMinutes,
        BigDecimal totalPayloadKg,
        List<RouteStop> stops,
        String geoJsonGeometry
) implements Iterable<RouteStop> {

    @Override
    public Iterator<RouteStop> iterator() {
        return new RouteStopIterator(stops);
    }
}

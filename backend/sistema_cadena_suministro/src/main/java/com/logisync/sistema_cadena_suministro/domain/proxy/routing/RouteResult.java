package com.logisync.sistema_cadena_suministro.domain.proxy.routing;

import java.util.List;

public record RouteResult(
        String routeKey,
        double totalDistanceKm,
        long durationMinutes,
        List<String> orderedStops,
        boolean fromCache
) {
    public RouteResult asCached() {
        return new RouteResult(routeKey, totalDistanceKm, durationMinutes, orderedStops, true);
    }
}

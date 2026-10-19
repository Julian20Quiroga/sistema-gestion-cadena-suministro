package com.logisync.sistema_cadena_suministro.domain.proxy.routing;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Proxy: Real Subject providing actual calculation logic.
 */
@Service
public class DirectRouteCalculationService implements RouteCalculationService {

    @Override
    public RouteResult calculateRoute(String origin, List<String> destinations) {
        List<String> stops = new ArrayList<>();
        stops.add(origin);
        if (destinations != null) {
            stops.addAll(destinations);
        }
        double estimatedDistance = stops.size() * 12.5;
        long durationMin = (long) (estimatedDistance * 1.8);
        String key = origin + "->" + String.join(",", destinations != null ? destinations : List.of());
        return new RouteResult(key, estimatedDistance, durationMin, stops, false);
    }
}

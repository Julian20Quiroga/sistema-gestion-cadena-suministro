package com.logisync.sistema_cadena_suministro.domain.proxy.routing;

import java.util.List;

/**
 * Proxy: Subject interface defining route calculation operations.
 */
public interface RouteCalculationService {
    RouteResult calculateRoute(String origin, List<String> destinations);
}

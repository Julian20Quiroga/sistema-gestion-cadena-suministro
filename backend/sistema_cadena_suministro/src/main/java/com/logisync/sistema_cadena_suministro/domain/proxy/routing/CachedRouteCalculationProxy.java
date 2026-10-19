package com.logisync.sistema_cadena_suministro.domain.proxy.routing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Proxy: Surrogate that manages cache and metrics before delegating to the real calculation service.
 */
@Slf4j
@Component
@Primary
public class CachedRouteCalculationProxy implements RouteCalculationService {

    private final RouteCalculationService realService;
    private final Map<String, RouteResult> cache = new ConcurrentHashMap<>();

    public CachedRouteCalculationProxy(DirectRouteCalculationService realService) {
        this.realService = realService;
    }

    @Override
    public RouteResult calculateRoute(String origin, List<String> destinations) {
        String key = origin + "->" + String.join(",", destinations != null ? destinations : List.of());
        if (cache.containsKey(key)) {
            log.info("[Route Proxy] Cache hit for key: {}", key);
            return cache.get(key).asCached();
        }
        log.info("[Route Proxy] Cache miss for key: {}. Calculating via real service...", key);
        long start = System.currentTimeMillis();
        RouteResult result = realService.calculateRoute(origin, destinations);
        long elapsed = System.currentTimeMillis() - start;
        log.info("[Route Proxy] Computed route in {} ms", elapsed);
        cache.put(key, result);
        return result;
    }

    public int getCacheSize() {
        return cache.size();
    }

    public void clearCache() {
        cache.clear();
    }
}

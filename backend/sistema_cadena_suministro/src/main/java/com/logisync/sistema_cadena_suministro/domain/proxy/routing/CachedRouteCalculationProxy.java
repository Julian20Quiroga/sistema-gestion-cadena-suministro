package com.logisync.sistema_cadena_suministro.domain.proxy.routing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Proxy estructural para el cálculo y optimización de rutas de distribución.
 * Patrón aplicado: Proxy (actúa como intermediario controlando el acceso al servicio real
 * DirectRouteCalculationService mediante almacenamiento en caché en memoria y métricas de latencia).
 */
@Slf4j
@Component
@Primary
public class CachedRouteCalculationProxy implements RouteCalculationService {

    private final RouteCalculationService realService;
    private final Map<String, RouteResult> cache = new ConcurrentHashMap<>();

    /**
     * Constructor que inyecta la implementación real del servicio de cálculo de rutas.
     */
    public CachedRouteCalculationProxy(DirectRouteCalculationService realService) {
        this.realService = realService;
    }

    /**
     * Calcula la ruta de distribución aplicando caché en memoria concurrente.
     * Patrón aplicado: Proxy (si la consulta de origen-destinos ya fue procesada previamente,
     * retorna el resultado cacheado al instante; de lo contrario, delega al servicio real y cachea el valor).
     */
    @Override
    public RouteResult calculateRoute(String origin, List<String> destinations) {
        String key = origin + "->" + String.join(",", destinations != null ? destinations : List.of());
        if (cache.containsKey(key)) {
            log.info("[Route Proxy] Cache hit para la ruta: {}", key);
            return cache.get(key).asCached();
        }
        log.info("[Route Proxy] Cache miss para la ruta: {}. Delegando al servicio real de cálculo...", key);
        long start = System.currentTimeMillis();
        RouteResult result = realService.calculateRoute(origin, destinations);
        long elapsed = System.currentTimeMillis() - start;
        log.info("[Route Proxy] Ruta calculada en {} ms", elapsed);
        cache.put(key, result);
        return result;
    }

    /**
     * Retorna la cantidad actual de rutas almacenadas en la caché en memoria.
     */
    public int getCacheSize() {
        return cache.size();
    }

    /**
     * Invalida y limpia todos los registros cacheados en memoria.
     */
    public void clearCache() {
        cache.clear();
    }
}

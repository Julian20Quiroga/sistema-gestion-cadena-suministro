package com.logisync.sistema_cadena_suministro.domain.routes.strategy;

import com.logisync.sistema_cadena_suministro.domain.routes.OptimizedRoute;
import com.logisync.sistema_cadena_suministro.domain.routes.RouteOptimizationContext;

/**
 * Interfaz de estrategia para algoritmos de optimización de rutas vehiculares.
 * Patrón aplicado: Strategy (define una familia de algoritmos intercambiables en tiempo de ejecución:
 * ShortestDistanceStrategy con fórmula Haversine, FastestTimeStrategy y BalancedStrategy).
 */
public interface RouteOptimizationStrategy {

    /**
     * Retorna el nombre identificador de la estrategia de optimización.
     */
    String getStrategyName();

    /**
     * Ejecuta el cálculo algorítmico ordenando las paradas y estimando distancia y duración total.
     * Patrón aplicado: Strategy (algoritmo concreto de optimización).
     */
    OptimizedRoute optimize(RouteOptimizationContext context);
}

package com.logisync.sistema_cadena_suministro.domain.routes.strategy;

import com.logisync.sistema_cadena_suministro.domain.routes.OptimizedRoute;
import com.logisync.sistema_cadena_suministro.domain.routes.RouteOptimizationContext;

/**
 * Strategy: Interface defining route optimization algorithms.
 */
public interface RouteOptimizationStrategy {
    String getStrategyName();
    OptimizedRoute optimize(RouteOptimizationContext context);
}

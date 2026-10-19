package com.logisync.sistema_cadena_suministro.domain.routes.strategy;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RouteStrategyResolver {

    private final Map<String, RouteOptimizationStrategy> strategies;

    public RouteStrategyResolver(Map<String, RouteOptimizationStrategy> strategies) {
        this.strategies = strategies;
    }

    public RouteOptimizationStrategy resolve(String name) {
        if (name == null || name.isBlank()) {
            return strategies.get("NEAREST_NEIGHBOR");
        }
        RouteOptimizationStrategy strategy = strategies.get(name.toUpperCase());
        if (strategy == null) {
            strategy = strategies.get("NEAREST_NEIGHBOR");
        }
        return strategy;
    }
}

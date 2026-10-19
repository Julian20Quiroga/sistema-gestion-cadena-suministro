package com.logisync.sistema_cadena_suministro.domain.routes.strategy;

import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.routes.GeoPoint;
import com.logisync.sistema_cadena_suministro.domain.routes.HaversineDistanceCalculator;
import com.logisync.sistema_cadena_suministro.domain.routes.OptimizedRoute;
import com.logisync.sistema_cadena_suministro.domain.routes.RouteOptimizationContext;
import com.logisync.sistema_cadena_suministro.domain.routes.RouteStop;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Strategy: Greedy nearest-neighbor algorithm for vehicle routing.
 */
@Component("NEAREST_NEIGHBOR")
public class NearestNeighborStrategy implements RouteOptimizationStrategy {

    @Override
    public String getStrategyName() {
        return "NEAREST_NEIGHBOR";
    }

    @Override
    public OptimizedRoute optimize(RouteOptimizationContext context) {
        validateCapacity(context);

        List<GeoPoint> unvisited = new ArrayList<>(context.deliveryDestinations());
        List<RouteStop> stops = new ArrayList<>();

        GeoPoint current = context.originWarehouse();
        stops.add(new RouteStop(1, current.label(), current.latitude(), current.longitude(), null, 0.0));

        double totalDistance = 0.0;
        int seq = 2;

        while (!unvisited.isEmpty()) {
            GeoPoint nearest = null;
            double minDistance = Double.MAX_VALUE;

            for (GeoPoint candidate : unvisited) {
                double dist = HaversineDistanceCalculator.distanceKm(
                        current.latitude(), current.longitude(),
                        candidate.latitude(), candidate.longitude());
                if (dist < minDistance) {
                    minDistance = dist;
                    nearest = candidate;
                }
            }

            if (nearest != null) {
                unvisited.remove(nearest);
                totalDistance += minDistance;
                stops.add(new RouteStop(seq++, nearest.label(), nearest.latitude(), nearest.longitude(), nearest.orderId(), minDistance));
                current = nearest;
            }
        }

        double speedKmh = context.vehicle() != null && context.vehicle().getAverageSpeedKmh() != null
                ? context.vehicle().getAverageSpeedKmh().doubleValue()
                : 40.0;
        long durationMinutes = Math.round((totalDistance / Math.max(speedKmh, 10.0)) * 60.0);

        BigDecimal payload = context.deliveryDestinations().stream()
                .map(d -> d.weightKg() != null ? d.weightKg() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String geoJson = buildGeoJson(stops);

        return new OptimizedRoute(getStrategyName(), Math.round(totalDistance * 100.0) / 100.0,
                durationMinutes, payload, stops, geoJson);
    }

    protected void validateCapacity(RouteOptimizationContext context) {
        if (context.vehicle() != null && context.vehicle().getCapacityKg() != null) {
            BigDecimal totalWeight = context.deliveryDestinations().stream()
                    .map(d -> d.weightKg() != null ? d.weightKg() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (totalWeight.compareTo(context.vehicle().getCapacityKg()) > 0) {
                throw new BusinessRuleException("Total shipment weight " + totalWeight
                        + " kg exceeds vehicle capacity " + context.vehicle().getCapacityKg() + " kg");
            }
        }
    }

    private String buildGeoJson(List<RouteStop> stops) {
        StringBuilder sb = new StringBuilder("{\"type\":\"LineString\",\"coordinates\":[");
        for (int i = 0; i < stops.size(); i++) {
            RouteStop s = stops.get(i);
            sb.append("[").append(s.longitude()).append(",").append(s.latitude()).append("]");
            if (i < stops.size() - 1) sb.append(",");
        }
        sb.append("]}");
        return sb.toString();
    }
}

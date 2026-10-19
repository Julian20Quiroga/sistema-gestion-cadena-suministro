package com.logisync.sistema_cadena_suministro.domain.routes.strategy;

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
 * Strategy: 2-opt local search heuristic algorithm that untangles route intersections.
 */
@Component("TWO_OPT")
public class TwoOptStrategy extends NearestNeighborStrategy {

    @Override
    public String getStrategyName() {
        return "TWO_OPT";
    }

    @Override
    public OptimizedRoute optimize(RouteOptimizationContext context) {
        // Start from Nearest Neighbor initial solution
        OptimizedRoute initial = super.optimize(context);
        if (initial.stops().size() <= 3) {
            return new OptimizedRoute(getStrategyName(), initial.totalDistanceKm(),
                    initial.estimatedDurationMinutes(), initial.totalPayloadKg(), initial.stops(), initial.geoJsonGeometry());
        }

        List<RouteStop> currentStops = new ArrayList<>(initial.stops());
        boolean improved = true;

        while (improved) {
            improved = false;
            for (int i = 1; i < currentStops.size() - 1; i++) {
                for (int k = i + 1; k < currentStops.size(); k++) {
                    double delta = compute2OptDelta(currentStops, i, k);
                    if (delta < -0.01) {
                        reverseSubsequence(currentStops, i, k);
                        improved = true;
                    }
                }
            }
        }

        // Recompute distances and sequences
        List<RouteStop> reindexedStops = new ArrayList<>();
        double totalDistance = 0.0;
        RouteStop prev = currentStops.get(0);
        reindexedStops.add(new RouteStop(1, prev.name(), prev.latitude(), prev.longitude(), prev.orderId(), 0.0));

        for (int i = 1; i < currentStops.size(); i++) {
            RouteStop curr = currentStops.get(i);
            double dist = HaversineDistanceCalculator.distanceKm(
                    prev.latitude(), prev.longitude(), curr.latitude(), curr.longitude());
            totalDistance += dist;
            reindexedStops.add(new RouteStop(i + 1, curr.name(), curr.latitude(), curr.longitude(), curr.orderId(), dist));
            prev = curr;
        }

        double speedKmh = context.vehicle() != null && context.vehicle().getAverageSpeedKmh() != null
                ? context.vehicle().getAverageSpeedKmh().doubleValue()
                : 40.0;
        long durationMinutes = Math.round((totalDistance / Math.max(speedKmh, 10.0)) * 60.0);

        StringBuilder sb = new StringBuilder("{\"type\":\"LineString\",\"coordinates\":[");
        for (int i = 0; i < reindexedStops.size(); i++) {
            RouteStop s = reindexedStops.get(i);
            sb.append("[").append(s.longitude()).append(",").append(s.latitude()).append("]");
            if (i < reindexedStops.size() - 1) sb.append(",");
        }
        sb.append("]}");

        return new OptimizedRoute(getStrategyName(), Math.round(totalDistance * 100.0) / 100.0,
                durationMinutes, initial.totalPayloadKg(), reindexedStops, sb.toString());
    }

    private double compute2OptDelta(List<RouteStop> stops, int i, int k) {
        RouteStop a = stops.get(i - 1);
        RouteStop b = stops.get(i);
        RouteStop c = stops.get(k);
        RouteStop d = (k + 1 < stops.size()) ? stops.get(k + 1) : null;

        double currentDist = HaversineDistanceCalculator.distanceKm(a.latitude(), a.longitude(), b.latitude(), b.longitude());
        if (d != null) {
            currentDist += HaversineDistanceCalculator.distanceKm(c.latitude(), c.longitude(), d.latitude(), d.longitude());
        }

        double newDist = HaversineDistanceCalculator.distanceKm(a.latitude(), a.longitude(), c.latitude(), c.longitude());
        if (d != null) {
            newDist += HaversineDistanceCalculator.distanceKm(b.latitude(), b.longitude(), d.latitude(), d.longitude());
        }

        return newDist - currentDist;
    }

    private void reverseSubsequence(List<RouteStop> stops, int i, int k) {
        while (i < k) {
            RouteStop temp = stops.get(i);
            stops.set(i, stops.get(k));
            stops.set(k, temp);
            i++;
            k--;
        }
    }
}

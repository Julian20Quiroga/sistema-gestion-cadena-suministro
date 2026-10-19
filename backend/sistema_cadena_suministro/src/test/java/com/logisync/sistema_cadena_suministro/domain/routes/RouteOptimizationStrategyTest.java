package com.logisync.sistema_cadena_suministro.domain.routes;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.models.Vehicle;
import com.logisync.sistema_cadena_suministro.domain.routes.strategy.NearestNeighborStrategy;
import com.logisync.sistema_cadena_suministro.domain.routes.strategy.TwoOptStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RouteOptimizationStrategyTest {

    @Test
    void nearestNeighborComputesOrderedSequenceAndTotalDistance() {
        Vehicle vehicle = new Vehicle(1, "ABC-123", "Van", new BigDecimal("1000.00"), new BigDecimal("40.00"), "hash", SoftDeleteStatus.ACTIVE);
        GeoPoint warehouse = new GeoPoint("Warehouse", new BigDecimal("4.609710"), new BigDecimal("-74.081750"), null, BigDecimal.ZERO);

        GeoPoint stop1 = new GeoPoint("Stop 1 (Close)", new BigDecimal("4.615000"), new BigDecimal("-74.080000"), 101, new BigDecimal("50.0"));
        GeoPoint stop2 = new GeoPoint("Stop 2 (Far)", new BigDecimal("4.750000"), new BigDecimal("-74.030000"), 102, new BigDecimal("80.0"));

        NearestNeighborStrategy strategy = new NearestNeighborStrategy();
        OptimizedRoute route = strategy.optimize(new RouteOptimizationContext(warehouse, List.of(stop2, stop1), vehicle));

        assertEquals("NEAREST_NEIGHBOR", route.strategyUsed());
        assertEquals(3, route.stops().size());
        assertEquals("Warehouse", route.stops().get(0).name());
        assertEquals("Stop 1 (Close)", route.stops().get(1).name()); // Close visited before far
        assertEquals("Stop 2 (Far)", route.stops().get(2).name());
        assertTrue(route.totalDistanceKm() > 0);
        assertNotNull(route.geoJsonGeometry());
    }

    @Test
    void throwsExceptionWhenPayloadExceedsVehicleCapacity() {
        Vehicle vehicle = new Vehicle(1, "ABC-123", "Van", new BigDecimal("100.00"), new BigDecimal("40.00"), "hash", SoftDeleteStatus.ACTIVE);
        GeoPoint warehouse = new GeoPoint("Warehouse", new BigDecimal("4.609710"), new BigDecimal("-74.081750"), null, BigDecimal.ZERO);
        GeoPoint heavyStop = new GeoPoint("Heavy Order", new BigDecimal("4.615000"), new BigDecimal("-74.080000"), 101, new BigDecimal("150.0"));

        NearestNeighborStrategy strategy = new NearestNeighborStrategy();
        assertThrows(BusinessRuleException.class,
                () -> strategy.optimize(new RouteOptimizationContext(warehouse, List.of(heavyStop), vehicle)));
    }

    @Test
    void twoOptStrategyOptimizesRoute() {
        Vehicle vehicle = new Vehicle(1, "ABC-123", "Van", new BigDecimal("1000.00"), new BigDecimal("40.00"), "hash", SoftDeleteStatus.ACTIVE);
        GeoPoint warehouse = new GeoPoint("Warehouse", new BigDecimal("4.609710"), new BigDecimal("-74.081750"), null, BigDecimal.ZERO);

        GeoPoint s1 = new GeoPoint("S1", new BigDecimal("4.620000"), new BigDecimal("-74.070000"), 1, new BigDecimal("10.0"));
        GeoPoint s2 = new GeoPoint("S2", new BigDecimal("4.650000"), new BigDecimal("-74.060000"), 2, new BigDecimal("10.0"));
        GeoPoint s3 = new GeoPoint("S3", new BigDecimal("4.680000"), new BigDecimal("-74.050000"), 3, new BigDecimal("10.0"));

        TwoOptStrategy strategy = new TwoOptStrategy();
        OptimizedRoute route = strategy.optimize(new RouteOptimizationContext(warehouse, List.of(s3, s1, s2), vehicle));

        assertEquals("TWO_OPT", route.strategyUsed());
        assertEquals(4, route.stops().size());

        // Verify Iterable support
        int count = 0;
        for (RouteStop stop : route) {
            count++;
            assertNotNull(stop.name());
        }
        assertEquals(4, count);
    }
}

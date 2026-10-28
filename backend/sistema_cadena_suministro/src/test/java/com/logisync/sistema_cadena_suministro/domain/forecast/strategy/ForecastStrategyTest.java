package com.logisync.sistema_cadena_suministro.domain.forecast.strategy;

import com.logisync.sistema_cadena_suministro.domain.models.ForecastResult;
import com.logisync.sistema_cadena_suministro.domain.models.HistoricalDataPoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ForecastStrategyTest {

    private final List<HistoricalDataPoint> history = List.of(
            new HistoricalDataPoint(1, 10.0),
            new HistoricalDataPoint(2, 20.0),
            new HistoricalDataPoint(3, 30.0)
    );

    @Test
    void movingAverageCalculatesAverageOfLastN() {
        ForecastStrategy strategy = new MovingAverageForecastStrategy(2);
        ForecastResult res = strategy.forecast(history, 2);

        // Last 2 are 20 and 30 -> avg = 25
        assertEquals(2, res.projectedQuantities().size());
        assertEquals(25.0, res.projectedQuantities().get(0));
        assertEquals(50.0, res.recommendedProcurementQuantity());
    }

    @Test
    void exponentialSmoothingWeightsRecentTrends() {
        ForecastStrategy strategy = new ExponentialSmoothingForecastStrategy(0.5);
        ForecastResult res = strategy.forecast(history, 3);

        assertEquals(3, res.projectedQuantities().size());
        assertTrue(res.recommendedProcurementQuantity() > 0);
    }

    @Test
    void linearRegressionProjectsTrendLine() {
        ForecastStrategy strategy = new LinearRegressionForecastStrategy();
        ForecastResult res = strategy.forecast(history, 2);

        // Slope is 10, next should be 40 and 50
        assertEquals(2, res.projectedQuantities().size());
        assertEquals(40.0, res.projectedQuantities().get(0));
        assertEquals(50.0, res.projectedQuantities().get(1));
        assertEquals(90.0, res.recommendedProcurementQuantity());
    }
}

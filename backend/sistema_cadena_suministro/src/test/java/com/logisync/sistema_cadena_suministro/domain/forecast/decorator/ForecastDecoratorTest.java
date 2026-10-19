package com.logisync.sistema_cadena_suministro.domain.forecast.decorator;

import com.logisync.sistema_cadena_suministro.domain.forecast.strategy.ForecastStrategy;
import com.logisync.sistema_cadena_suministro.domain.forecast.strategy.MovingAverageForecastStrategy;
import com.logisync.sistema_cadena_suministro.domain.models.ForecastResult;
import com.logisync.sistema_cadena_suministro.domain.models.HistoricalDataPoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ForecastDecoratorTest {

    private final List<HistoricalDataPoint> history = List.of(
            new HistoricalDataPoint(1, 100.0),
            new HistoricalDataPoint(2, 100.0)
    );

    @Test
    void seasonalDecoratorMultipliesProjections() {
        ForecastStrategy base = new MovingAverageForecastStrategy(2);
        ForecastStrategy seasonal = new SeasonalAdjustmentDecorator(base, 1.25); // +25%

        ForecastResult res = seasonal.forecast(history, 2);
        assertEquals(125.0, res.projectedQuantities().get(0));
        assertEquals(250.0, res.recommendedProcurementQuantity());
        assertTrue(res.algorithmName().contains("SEASONAL"));
    }

    @Test
    void safetyStockDecoratorAddsMargin() {
        ForecastStrategy base = new MovingAverageForecastStrategy(2);
        ForecastStrategy withSafety = new SafetyStockDecorator(base, 0.20); // +20% buffer

        ForecastResult res = withSafety.forecast(history, 2);
        // Base is 200 total, +20% = 240
        assertEquals(240.0, res.recommendedProcurementQuantity());
        assertTrue(res.algorithmName().contains("SAFETY_STOCK"));
    }
}

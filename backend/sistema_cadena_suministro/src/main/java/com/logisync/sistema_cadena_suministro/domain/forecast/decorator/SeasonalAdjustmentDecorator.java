package com.logisync.sistema_cadena_suministro.domain.forecast.decorator;

import com.logisync.sistema_cadena_suministro.domain.forecast.strategy.ForecastStrategy;
import com.logisync.sistema_cadena_suministro.domain.models.ForecastResult;
import com.logisync.sistema_cadena_suministro.domain.models.HistoricalDataPoint;

import java.util.List;

/**
 * Decorator: Adjusts base projections by a seasonal demand multiplier (e.g., 1.20 for +20% high season).
 */
public class SeasonalAdjustmentDecorator extends ForecastDecorator {

    private final double seasonalMultiplier;

    public SeasonalAdjustmentDecorator(ForecastStrategy delegate, double seasonalMultiplier) {
        super(delegate);
        this.seasonalMultiplier = Math.max(0.1, seasonalMultiplier);
    }

    @Override
    public String getAlgorithmName() {
        return delegate.getAlgorithmName() + "+SEASONAL(" + seasonalMultiplier + "x)";
    }

    @Override
    public ForecastResult forecast(List<HistoricalDataPoint> history, int periodsAhead) {
        ForecastResult base = delegate.forecast(history, periodsAhead);
        List<Double> adjusted = base.projectedQuantities().stream()
                .map(q -> Math.round((q * seasonalMultiplier) * 100.0) / 100.0)
                .toList();
        double total = adjusted.stream().mapToDouble(Double::doubleValue).sum();
        return new ForecastResult(getAlgorithmName(), adjusted, Math.round(total * 100.0) / 100.0);
    }
}

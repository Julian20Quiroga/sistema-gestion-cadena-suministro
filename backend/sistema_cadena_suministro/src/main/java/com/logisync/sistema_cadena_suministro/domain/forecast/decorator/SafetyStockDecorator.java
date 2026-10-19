package com.logisync.sistema_cadena_suministro.domain.forecast.decorator;

import com.logisync.sistema_cadena_suministro.domain.forecast.strategy.ForecastStrategy;
import com.logisync.sistema_cadena_suministro.domain.models.ForecastResult;
import com.logisync.sistema_cadena_suministro.domain.models.HistoricalDataPoint;

import java.util.List;

/**
 * Decorator: Adds safety stock buffer to total recommended procurement quantity.
 */
public class SafetyStockDecorator extends ForecastDecorator {

    private final double bufferPercentage;

    public SafetyStockDecorator(ForecastStrategy delegate, double bufferPercentage) {
        super(delegate);
        this.bufferPercentage = Math.max(0.0, bufferPercentage);
    }

    @Override
    public String getAlgorithmName() {
        return delegate.getAlgorithmName() + "+SAFETY_STOCK(" + (int)(bufferPercentage * 100) + "%)";
    }

    @Override
    public ForecastResult forecast(List<HistoricalDataPoint> history, int periodsAhead) {
        ForecastResult base = delegate.forecast(history, periodsAhead);
        double totalWithBuffer = Math.round((base.recommendedProcurementQuantity() * (1.0 + bufferPercentage)) * 100.0) / 100.0;
        return new ForecastResult(getAlgorithmName(), base.projectedQuantities(), totalWithBuffer);
    }
}

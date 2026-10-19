package com.logisync.sistema_cadena_suministro.domain.forecast.strategy;

import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.models.ForecastResult;
import com.logisync.sistema_cadena_suministro.domain.models.HistoricalDataPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy: Single Exponential Smoothing with alpha weighting factor.
 */
public class ExponentialSmoothingForecastStrategy implements ForecastStrategy {

    private final double alpha;

    public ExponentialSmoothingForecastStrategy(double alpha) {
        if (alpha <= 0.0 || alpha > 1.0) {
            throw new BusinessRuleException("Alpha must be between 0 and 1, received: " + alpha);
        }
        this.alpha = alpha;
    }

    public ExponentialSmoothingForecastStrategy() {
        this(0.35);
    }

    @Override
    public String getAlgorithmName() {
        return "EXPONENTIAL_SMOOTHING";
    }

    @Override
    public ForecastResult forecast(List<HistoricalDataPoint> history, int periodsAhead) {
        if (history == null || history.isEmpty()) {
            throw new BusinessRuleException("Historical demand data cannot be empty for forecasting");
        }
        int effectivePeriods = Math.max(1, periodsAhead);

        double s = history.get(0).quantity();
        for (int i = 1; i < history.size(); i++) {
            s = alpha * history.get(i).quantity() + (1.0 - alpha) * s;
        }
        double smoothed = Math.round(s * 100.0) / 100.0;

        List<Double> projected = new ArrayList<>();
        double total = 0.0;
        for (int p = 0; p < effectivePeriods; p++) {
            projected.add(smoothed);
            total += smoothed;
        }

        return new ForecastResult(getAlgorithmName(), projected, Math.round(total * 100.0) / 100.0);
    }
}

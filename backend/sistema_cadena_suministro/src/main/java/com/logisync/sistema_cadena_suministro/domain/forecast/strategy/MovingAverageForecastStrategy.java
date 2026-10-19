package com.logisync.sistema_cadena_suministro.domain.forecast.strategy;

import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.models.ForecastResult;
import com.logisync.sistema_cadena_suministro.domain.models.HistoricalDataPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy: Simple Moving Average forecasting algorithm.
 */
public class MovingAverageForecastStrategy implements ForecastStrategy {

    private final int windowSize;

    public MovingAverageForecastStrategy(int windowSize) {
        this.windowSize = Math.max(1, windowSize);
    }

    public MovingAverageForecastStrategy() {
        this(3);
    }

    @Override
    public String getAlgorithmName() {
        return "MOVING_AVERAGE";
    }

    @Override
    public ForecastResult forecast(List<HistoricalDataPoint> history, int periodsAhead) {
        if (history == null || history.isEmpty()) {
            throw new BusinessRuleException("Historical demand data cannot be empty for forecasting");
        }
        int effectivePeriods = Math.max(1, periodsAhead);
        int n = Math.min(windowSize, history.size());

        // Calculate average over the last n observations
        double sum = 0.0;
        int start = history.size() - n;
        for (int i = start; i < history.size(); i++) {
            sum += history.get(i).quantity();
        }
        double avg = Math.round((sum / n) * 100.0) / 100.0;

        List<Double> projected = new ArrayList<>();
        double totalDemand = 0.0;
        for (int p = 0; p < effectivePeriods; p++) {
            projected.add(avg);
            totalDemand += avg;
        }

        return new ForecastResult(getAlgorithmName(), projected, Math.round(totalDemand * 100.0) / 100.0);
    }
}

package com.logisync.sistema_cadena_suministro.domain.forecast.strategy;

import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.models.ForecastResult;
import com.logisync.sistema_cadena_suministro.domain.models.HistoricalDataPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy: Ordinary Least Squares Linear Regression trend forecasting.
 */
public class LinearRegressionForecastStrategy implements ForecastStrategy {

    @Override
    public String getAlgorithmName() {
        return "LINEAR_REGRESSION";
    }

    @Override
    public ForecastResult forecast(List<HistoricalDataPoint> history, int periodsAhead) {
        if (history == null || history.isEmpty()) {
            throw new BusinessRuleException("Historical demand data cannot be empty for forecasting");
        }
        int effectivePeriods = Math.max(1, periodsAhead);
        int n = history.size();

        if (n == 1) {
            double val = history.get(0).quantity();
            List<Double> proj = new ArrayList<>();
            for (int i = 0; i < effectivePeriods; i++) proj.add(val);
            return new ForecastResult(getAlgorithmName(), proj, val * effectivePeriods);
        }

        double sumX = 0.0;
        double sumY = 0.0;
        double sumXY = 0.0;
        double sumX2 = 0.0;

        for (int i = 0; i < n; i++) {
            double x = i + 1;
            double y = history.get(i).quantity();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double denom = (n * sumX2 - sumX * sumX);
        double slope = (denom == 0) ? 0.0 : (n * sumXY - sumX * sumY) / denom;
        double intercept = (sumY - slope * sumX) / n;

        List<Double> projected = new ArrayList<>();
        double total = 0.0;
        for (int p = 1; p <= effectivePeriods; p++) {
            double xPred = n + p;
            double pred = Math.max(0.0, Math.round((slope * xPred + intercept) * 100.0) / 100.0);
            projected.add(pred);
            total += pred;
        }

        return new ForecastResult(getAlgorithmName(), projected, Math.round(total * 100.0) / 100.0);
    }
}

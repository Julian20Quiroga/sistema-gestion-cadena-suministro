package com.logisync.sistema_cadena_suministro.domain.forecast.strategy;

import com.logisync.sistema_cadena_suministro.domain.models.ForecastResult;
import com.logisync.sistema_cadena_suministro.domain.models.HistoricalDataPoint;

import java.util.List;

/**
 * Strategy Pattern: Component interface for pluggable demand forecasting algorithms.
 */
public interface ForecastStrategy {
    String getAlgorithmName();
    ForecastResult forecast(List<HistoricalDataPoint> history, int periodsAhead);
}

package com.logisync.sistema_cadena_suministro.domain.forecast.strategy;

import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;

public final class ForecastStrategyFactory {

    private ForecastStrategyFactory() {}

    public static ForecastStrategy create(String algorithm) {
        if (algorithm == null || algorithm.isBlank() || algorithm.equalsIgnoreCase("MOVING_AVERAGE")) {
            return new MovingAverageForecastStrategy(3);
        }
        if (algorithm.equalsIgnoreCase("EXPONENTIAL_SMOOTHING")) {
            return new ExponentialSmoothingForecastStrategy(0.35);
        }
        if (algorithm.equalsIgnoreCase("LINEAR_REGRESSION")) {
            return new LinearRegressionForecastStrategy();
        }
        throw new BusinessRuleException("Unknown forecast algorithm: " + algorithm +
                ". Supported: MOVING_AVERAGE, EXPONENTIAL_SMOOTHING, LINEAR_REGRESSION");
    }
}

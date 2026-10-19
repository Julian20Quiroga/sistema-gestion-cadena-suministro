package com.logisync.sistema_cadena_suministro.domain.forecast.decorator;

import com.logisync.sistema_cadena_suministro.domain.forecast.strategy.ForecastStrategy;

/**
 * Decorator: Base decorator wrapping a ForecastStrategy to enrich calculations.
 */
public abstract class ForecastDecorator implements ForecastStrategy {

    protected final ForecastStrategy delegate;

    protected ForecastDecorator(ForecastStrategy delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getAlgorithmName() {
        return delegate.getAlgorithmName();
    }
}

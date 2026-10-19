package com.logisync.sistema_cadena_suministro.domain.bridge.telemetry;

/**
 * Bridge: Abstraction defining the interface for telemetry publishing.
 */
public abstract class TelemetryPublication {

    protected final TelemetrySink sink;

    protected TelemetryPublication(TelemetrySink sink) {
        this.sink = sink;
    }

    public abstract void publish(TelemetryData data);

    public TelemetrySink getSink() {
        return sink;
    }
}

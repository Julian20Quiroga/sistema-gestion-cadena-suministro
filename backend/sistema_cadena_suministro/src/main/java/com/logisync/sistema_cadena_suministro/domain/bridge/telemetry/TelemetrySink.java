package com.logisync.sistema_cadena_suministro.domain.bridge.telemetry;

/**
 * Bridge: Implementor interface decoupling how telemetry data is consumed/exported.
 */
public interface TelemetrySink {
    void emit(TelemetryData data);
    String getSinkName();
}

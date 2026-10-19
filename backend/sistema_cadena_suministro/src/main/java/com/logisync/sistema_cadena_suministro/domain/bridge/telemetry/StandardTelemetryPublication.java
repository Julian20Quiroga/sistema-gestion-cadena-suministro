package com.logisync.sistema_cadena_suministro.domain.bridge.telemetry;

/**
 * Bridge: Refined abstraction for standard telemetry dispatching.
 */
public class StandardTelemetryPublication extends TelemetryPublication {

    public StandardTelemetryPublication(TelemetrySink sink) {
        super(sink);
    }

    @Override
    public void publish(TelemetryData data) {
        sink.emit(data);
    }
}

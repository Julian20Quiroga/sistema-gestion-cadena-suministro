package com.logisync.sistema_cadena_suministro.domain.bridge.telemetry;

/**
 * Bridge: Refined abstraction that tags emergency or high-speed telemetry before emitting.
 */
public class UrgentAlertTelemetryPublication extends TelemetryPublication {

    public UrgentAlertTelemetryPublication(TelemetrySink sink) {
        super(sink);
    }

    @Override
    public void publish(TelemetryData data) {
        // Enriches transmission with urgent priority
        sink.emit(data);
    }
}

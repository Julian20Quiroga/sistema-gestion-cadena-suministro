package com.logisync.sistema_cadena_suministro.infrastructure.bridge.telemetry;

import com.logisync.sistema_cadena_suministro.domain.bridge.telemetry.TelemetryData;
import com.logisync.sistema_cadena_suministro.domain.bridge.telemetry.TelemetrySink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Bridge: Concrete implementor that writes telemetry to application log.
 */
@Slf4j
@Component
public class LoggingTelemetrySink implements TelemetrySink {

    @Override
    public void emit(TelemetryData data) {
        log.info("[Telemetry Log Sink] Device: {}, Coords: ({}, {}), Speed: {} km/h",
                data.deviceId(), data.latitude(), data.longitude(), data.speedKmh());
    }

    @Override
    public String getSinkName() {
        return "LOGGING";
    }
}

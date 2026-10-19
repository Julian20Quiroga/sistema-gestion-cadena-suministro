package com.logisync.sistema_cadena_suministro.infrastructure.bridge.telemetry;

import com.logisync.sistema_cadena_suministro.domain.bridge.telemetry.TelemetryData;
import com.logisync.sistema_cadena_suministro.domain.bridge.telemetry.TelemetrySink;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Bridge: Concrete implementor adapter that integrates with Grafana/Prometheus metrics endpoints.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GrafanaBridgeAdapterSink implements TelemetrySink {

    private final MeterRegistry meterRegistry;

    @Override
    public void emit(TelemetryData data) {
        meterRegistry.counter("logisync.telemetry.readings", "device", data.deviceId()).increment();
        meterRegistry.gauge("logisync.vehicle.speed", Tags.of("device", data.deviceId()), data.speedKmh());
        log.debug("[Grafana Bridge Sink] Exported Prometheus metric for device {}", data.deviceId());
    }

    @Override
    public String getSinkName() {
        return "GRAFANA_ADAPTER";
    }
}

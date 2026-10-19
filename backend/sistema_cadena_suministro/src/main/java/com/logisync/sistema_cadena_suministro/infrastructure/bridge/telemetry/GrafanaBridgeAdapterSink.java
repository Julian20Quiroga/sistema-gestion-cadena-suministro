package com.logisync.sistema_cadena_suministro.infrastructure.bridge.telemetry;

import com.logisync.sistema_cadena_suministro.domain.bridge.telemetry.TelemetryData;
import com.logisync.sistema_cadena_suministro.domain.bridge.telemetry.TelemetrySink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Bridge: Concrete implementor adapter that integrates with Grafana/Prometheus metrics endpoints.
 */
@Slf4j
@Component
public class GrafanaBridgeAdapterSink implements TelemetrySink {

    @Override
    public void emit(TelemetryData data) {
        log.debug("[Grafana Bridge Sink] Exporting Prometheus metric for device {}", data.deviceId());
    }

    @Override
    public String getSinkName() {
        return "GRAFANA_ADAPTER";
    }
}

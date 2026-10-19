package com.logisync.sistema_cadena_suministro.infrastructure.bridge.telemetry;

import com.logisync.sistema_cadena_suministro.domain.bridge.telemetry.TelemetryData;
import com.logisync.sistema_cadena_suministro.domain.bridge.telemetry.TelemetrySink;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Bridge: Concrete implementor that streams telemetry to in-memory buffers (SSE/WebSocket feed).
 */
@Component
public class StreamTelemetrySink implements TelemetrySink {

    private final Queue<TelemetryData> buffer = new ConcurrentLinkedQueue<>();

    @Override
    public void emit(TelemetryData data) {
        buffer.offer(data);
        if (buffer.size() > 500) {
            buffer.poll();
        }
    }

    @Override
    public String getSinkName() {
        return "STREAM";
    }

    public int getBufferSize() {
        return buffer.size();
    }
}

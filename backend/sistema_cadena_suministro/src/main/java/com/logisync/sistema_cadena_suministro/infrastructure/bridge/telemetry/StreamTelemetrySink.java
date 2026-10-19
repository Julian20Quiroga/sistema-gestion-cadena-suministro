package com.logisync.sistema_cadena_suministro.infrastructure.bridge.telemetry;

import com.logisync.sistema_cadena_suministro.domain.bridge.telemetry.TelemetryData;
import com.logisync.sistema_cadena_suministro.domain.bridge.telemetry.TelemetrySink;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Bridge: Concrete implementor that streams telemetry to in-memory buffers and SSE/WebSocket subscribers.
 */
@Component
public class StreamTelemetrySink implements TelemetrySink {

    private final Queue<TelemetryData> buffer = new ConcurrentLinkedQueue<>();
    private final List<Consumer<TelemetryData>> subscribers = new CopyOnWriteArrayList<>();

    @Override
    public void emit(TelemetryData data) {
        buffer.offer(data);
        if (buffer.size() > 500) {
            buffer.poll();
        }
        for (Consumer<TelemetryData> sub : subscribers) {
            try {
                sub.accept(data);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public String getSinkName() {
        return "STREAM";
    }

    public int getBufferSize() {
        return buffer.size();
    }

    public void subscribe(Consumer<TelemetryData> listener) {
        subscribers.add(listener);
    }

    public void unsubscribe(Consumer<TelemetryData> listener) {
        subscribers.remove(listener);
    }
}

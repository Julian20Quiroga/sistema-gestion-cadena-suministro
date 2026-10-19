package com.logisync.sistema_cadena_suministro.infrastructure.controllers;

import com.logisync.sistema_cadena_suministro.application.ports.driven.IngestTelemetryUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryTrackingUseCase;
import com.logisync.sistema_cadena_suministro.domain.bridge.telemetry.TelemetryData;
import com.logisync.sistema_cadena_suministro.infrastructure.bridge.telemetry.StreamTelemetrySink;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.IngestTelemetryRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.TelemetryReadingResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.VehicleTrailResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers.TrackingRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.function.Consumer;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final IngestTelemetryUseCase ingestTelemetryUseCase;
    private final QueryTrackingUseCase queryTrackingUseCase;
    private final TrackingRestMapper mapper;
    private final StreamTelemetrySink streamTelemetrySink;

    @PostMapping("/telemetry")
    public ResponseEntity<TelemetryReadingResponse> ingest(
            @RequestHeader(value = "X-Vehicle-Api-Key", required = false) String headerKey,
            @RequestParam(value = "apiKey", required = false) String queryKey,
            @Valid @RequestBody IngestTelemetryRequest request) {
        String apiKey = (headerKey != null && !headerKey.isBlank()) ? headerKey : queryKey;
        var appResponse = ingestTelemetryUseCase.ingest(apiKey, mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(appResponse));
    }

    @GetMapping("/vehicles/{id}/latest")
    public ResponseEntity<TelemetryReadingResponse> getLatestByVehicle(@PathVariable("id") Integer vehicleId) {
        return ResponseEntity.ok(mapper.toResponse(queryTrackingUseCase.getLatestByVehicleId(vehicleId)));
    }

    @GetMapping("/vehicles/{id}/trail")
    public ResponseEntity<VehicleTrailResponse> getTrail(
            @PathVariable("id") Integer vehicleId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return ResponseEntity.ok(mapper.toResponse(queryTrackingUseCase.getTrailByVehicleId(vehicleId, limit)));
    }

    @GetMapping("/dispatches/{id}/latest")
    public ResponseEntity<TelemetryReadingResponse> getLatestByDispatch(@PathVariable("id") Integer dispatchId) {
        return ResponseEntity.ok(mapper.toResponse(queryTrackingUseCase.getLatestByDispatchId(dispatchId)));
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamTelemetry() {
        SseEmitter emitter = new SseEmitter(180_000L); // 3-minute timeout
        Consumer<TelemetryData> listener = data -> {
            try {
                emitter.send(SseEmitter.event().name("telemetry").data(data));
            } catch (IOException ex) {
                emitter.completeWithError(ex);
            }
        };

        streamTelemetrySink.subscribe(listener);
        emitter.onCompletion(() -> streamTelemetrySink.unsubscribe(listener));
        emitter.onTimeout(() -> streamTelemetrySink.unsubscribe(listener));
        emitter.onError(e -> streamTelemetrySink.unsubscribe(listener));

        return emitter;
    }
}

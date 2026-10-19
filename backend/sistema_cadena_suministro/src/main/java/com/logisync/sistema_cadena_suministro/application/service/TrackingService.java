package com.logisync.sistema_cadena_suministro.application.service;

import com.logisync.sistema_cadena_suministro.application.commands.IngestTelemetryCommand;
import com.logisync.sistema_cadena_suministro.application.ports.driven.IngestTelemetryUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryTrackingUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.DispatchRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.TelemetryRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.VehicleRepository;
import com.logisync.sistema_cadena_suministro.application.responses.TelemetryReadingAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.VehicleTrailAppResponse;
import com.logisync.sistema_cadena_suministro.domain.bridge.telemetry.StandardTelemetryPublication;
import com.logisync.sistema_cadena_suministro.domain.bridge.telemetry.TelemetryData;
import com.logisync.sistema_cadena_suministro.domain.bridge.telemetry.TelemetryPublication;
import com.logisync.sistema_cadena_suministro.domain.bridge.telemetry.TelemetrySink;
import com.logisync.sistema_cadena_suministro.domain.bridge.telemetry.UrgentAlertTelemetryPublication;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ConflictException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.UnauthorizedException;
import com.logisync.sistema_cadena_suministro.domain.models.Dispatch;
import com.logisync.sistema_cadena_suministro.domain.models.TelemetryReading;
import com.logisync.sistema_cadena_suministro.domain.models.Vehicle;
import com.logisync.sistema_cadena_suministro.domain.tracking.chain.TelemetryValidationChain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackingService implements IngestTelemetryUseCase, QueryTrackingUseCase {

    private final TelemetryRepository telemetryRepository;
    private final VehicleRepository vehicleRepository;
    private final DispatchRepository dispatchRepository;
    private final List<TelemetrySink> telemetrySinks;

    private final TelemetryValidationChain validationChain = new TelemetryValidationChain();

    @Override
    @Transactional
    public TelemetryReadingAppResponse ingest(String apiKey, IngestTelemetryCommand command) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new UnauthorizedException("Vehicle API key is missing");
        }
        String keyHash = hashKey(apiKey.trim());
        Vehicle vehicle = vehicleRepository.findByApiKeyHash(keyHash)
                .orElseThrow(() -> new UnauthorizedException("Invalid vehicle API key"));

        // Validate via Chain of Responsibility
        validationChain.execute(command, vehicle);

        LocalDateTime recordedAt = command.recordedAt().truncatedTo(java.time.temporal.ChronoUnit.SECONDS);

        // Idempotency check
        if (telemetryRepository.existsByVehicleIdAndRecordedAt(vehicle.getId(), recordedAt)) {
            throw new ConflictException("Telemetry reading already recorded for vehicle " + vehicle.getId() + " at " + recordedAt);
        }

        TelemetryReading reading = new TelemetryReading(
                null,
                vehicle.getId(),
                command.latitude(),
                command.longitude(),
                command.speedKmh(),
                recordedAt,
                null
        );
        TelemetryReading saved = telemetryRepository.save(reading);

        // Bridge Pattern: Publish to sinks (Standard vs Urgent based on speed threshold)
        TelemetryData telemetryData = new TelemetryData(
                "VEHICLE-" + vehicle.getId(),
                command.latitude(),
                command.longitude(),
                command.speedKmh(),
                recordedAt
        );

        for (TelemetrySink sink : telemetrySinks) {
            TelemetryPublication publication = (command.speedKmh() > 100.0)
                    ? new UrgentAlertTelemetryPublication(sink)
                    : new StandardTelemetryPublication(sink);
            publication.publish(telemetryData);
        }

        return toAppResponse(saved, vehicle.getPlate());
    }

    @Override
    @Transactional(readOnly = true)
    public TelemetryReadingAppResponse getLatestByVehicleId(Integer vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", vehicleId));
        TelemetryReading latest = telemetryRepository.findLatestByVehicleId(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Telemetry reading not found for vehicle " + vehicleId));
        return toAppResponse(latest, vehicle.getPlate());
    }

    @Override
    @Transactional(readOnly = true)
    public TelemetryReadingAppResponse getLatestByDispatchId(Integer dispatchId) {
        Dispatch dispatch = dispatchRepository.findById(dispatchId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispatch", dispatchId));
        if (dispatch.getVehicleId() == null) {
            throw new ResourceNotFoundException("No vehicle assigned to dispatch " + dispatchId);
        }
        return getLatestByVehicleId(dispatch.getVehicleId());
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleTrailAppResponse getTrailByVehicleId(Integer vehicleId, int limit) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", vehicleId));
        int effectiveLimit = (limit <= 0 || limit > 500) ? 50 : limit;
        List<TelemetryReading> readings = telemetryRepository.findRecentByVehicleId(vehicleId, effectiveLimit);

        List<TelemetryReadingAppResponse> mapped = readings.stream()
                .map(r -> toAppResponse(r, vehicle.getPlate()))
                .toList();

        StringBuilder coordsJson = new StringBuilder("[");
        for (int i = 0; i < readings.size(); i++) {
            TelemetryReading r = readings.get(i);
            coordsJson.append("[").append(r.getLongitude()).append(",").append(r.getLatitude()).append("]");
            if (i < readings.size() - 1) {
                coordsJson.append(",");
            }
        }
        coordsJson.append("]");

        String geoJson = """
                {"type":"LineString","coordinates":%s}
                """.formatted(coordsJson.toString()).trim();

        return new VehicleTrailAppResponse(vehicle.getId(), vehicle.getPlate(), geoJson, mapped);
    }

    private TelemetryReadingAppResponse toAppResponse(TelemetryReading r, String plate) {
        return new TelemetryReadingAppResponse(
                r.getId(),
                r.getVehicleId(),
                plate,
                r.getLatitude(),
                r.getLongitude(),
                r.getSpeedKmh(),
                r.getRecordedAt(),
                r.getCreatedAt()
        );
    }

    private String hashKey(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}

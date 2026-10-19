package com.logisync.sistema_cadena_suministro.application.ports.driving;

import com.logisync.sistema_cadena_suministro.domain.models.TelemetryReading;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TelemetryRepository {
    TelemetryReading save(TelemetryReading reading);
    Optional<TelemetryReading> findLatestByVehicleId(Integer vehicleId);
    List<TelemetryReading> findRecentByVehicleId(Integer vehicleId, int limit);
    boolean existsByVehicleIdAndRecordedAt(Integer vehicleId, LocalDateTime recordedAt);
}

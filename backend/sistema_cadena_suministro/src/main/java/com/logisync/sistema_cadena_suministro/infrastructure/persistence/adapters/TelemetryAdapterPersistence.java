package com.logisync.sistema_cadena_suministro.infrastructure.persistence.adapters;

import com.logisync.sistema_cadena_suministro.application.ports.driving.TelemetryRepository;
import com.logisync.sistema_cadena_suministro.domain.models.TelemetryReading;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.TelemetryReadingDAO;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.TelemetryReadingEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers.TelemetryPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional
public class TelemetryAdapterPersistence implements TelemetryRepository {

    private final TelemetryReadingDAO dao;
    private final TelemetryPersistenceMapper mapper;

    @Override
    public TelemetryReading save(TelemetryReading reading) {
        TelemetryReadingEntity entity = mapper.toEntity(reading);
        TelemetryReadingEntity saved = dao.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<TelemetryReading> findLatestByVehicleId(Integer vehicleId) {
        return dao.findLatestByVehicleId(vehicleId).map(mapper::toDomain);
    }

    @Override
    public List<TelemetryReading> findRecentByVehicleId(Integer vehicleId, int limit) {
        return dao.findRecentByVehicleId(vehicleId, PageRequest.of(0, Math.max(1, limit)))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByVehicleIdAndRecordedAt(Integer vehicleId, LocalDateTime recordedAt) {
        return dao.existsByVehicleIdAndRecordedAt(vehicleId, recordedAt);
    }
}

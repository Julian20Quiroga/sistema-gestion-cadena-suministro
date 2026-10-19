package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.TelemetryReadingEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TelemetryReadingDAO extends JpaRepository<TelemetryReadingEntity, Integer> {

    @Query("SELECT t FROM TelemetryReadingEntity t WHERE t.vehicleId = :vehicleId ORDER BY t.recordedAt DESC")
    List<TelemetryReadingEntity> findRecentByVehicleId(@Param("vehicleId") Integer vehicleId, Pageable pageable);

    default Optional<TelemetryReadingEntity> findLatestByVehicleId(Integer vehicleId) {
        List<TelemetryReadingEntity> list = findRecentByVehicleId(vehicleId, Pageable.ofSize(1));
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    boolean existsByVehicleIdAndRecordedAt(Integer vehicleId, LocalDateTime recordedAt);
}

package com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers;

import com.logisync.sistema_cadena_suministro.domain.models.TelemetryReading;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.TelemetryReadingEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TelemetryPersistenceMapper {
    TelemetryReading toDomain(TelemetryReadingEntity entity);
    TelemetryReadingEntity toEntity(TelemetryReading domain);
}

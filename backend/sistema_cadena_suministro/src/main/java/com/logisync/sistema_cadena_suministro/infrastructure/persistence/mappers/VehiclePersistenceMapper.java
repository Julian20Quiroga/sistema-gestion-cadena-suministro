package com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers;

import com.logisync.sistema_cadena_suministro.domain.models.Vehicle;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.VehicleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VehiclePersistenceMapper {
    Vehicle toDomain(VehicleEntity entity);
    VehicleEntity toEntity(Vehicle domain);
}

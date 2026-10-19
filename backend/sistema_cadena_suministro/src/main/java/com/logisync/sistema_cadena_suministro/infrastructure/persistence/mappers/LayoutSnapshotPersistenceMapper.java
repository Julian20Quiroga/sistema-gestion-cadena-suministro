package com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers;

import com.logisync.sistema_cadena_suministro.domain.models.LayoutSnapshot;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.LayoutSnapshotEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LayoutSnapshotPersistenceMapper {

    @Mapping(target = "createdAt", source = "createdDate")
    LayoutSnapshot toDomain(LayoutSnapshotEntity entity);

    @Mapping(target = "createdDate", source = "createdAt")
    LayoutSnapshotEntity toEntity(LayoutSnapshot domain);
}

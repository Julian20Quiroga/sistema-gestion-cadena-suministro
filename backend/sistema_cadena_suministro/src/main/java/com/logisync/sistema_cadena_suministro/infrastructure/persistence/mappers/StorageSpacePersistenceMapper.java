package com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers;

import com.logisync.sistema_cadena_suministro.domain.models.StorageSpace;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.StorageSpaceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StorageSpacePersistenceMapper {

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    StorageSpaceEntity toEntity(StorageSpace space);

    StorageSpace toDomain(StorageSpaceEntity entity);
}

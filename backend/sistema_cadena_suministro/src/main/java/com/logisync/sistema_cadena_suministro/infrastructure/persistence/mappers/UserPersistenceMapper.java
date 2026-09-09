package com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.logisync.sistema_cadena_suministro.domain.models.User;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.UserEntity;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    User toDomain(UserEntity userEntity);

    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    UserEntity toEntity(User user);
}

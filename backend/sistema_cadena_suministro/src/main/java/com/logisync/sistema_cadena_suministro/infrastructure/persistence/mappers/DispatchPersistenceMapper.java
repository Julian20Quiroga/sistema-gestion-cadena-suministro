package com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers;

import com.logisync.sistema_cadena_suministro.domain.models.DetailDispatch;
import com.logisync.sistema_cadena_suministro.domain.models.Dispatch;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.DetailDispatchEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.DispatchEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DispatchPersistenceMapper {

    @Mapping(target = "details", source = "details")
    @Mapping(target = "createdAt", source = "createdDate")
    Dispatch toDomain(DispatchEntity entity);

    @Mapping(target = "dispatchId", source = "dispatch.id")
    DetailDispatch toDetailDomain(DetailDispatchEntity entity);

    @Mapping(target = "details", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    DispatchEntity toEntity(Dispatch domain);
}

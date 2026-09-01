package com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers;

import com.logisync.sistema_cadena_suministro.domain.models.Supplier;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.SupplierEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SupplierPersistenceMapper {

    SupplierEntity toEntity(Supplier supplier);
    Supplier toDomain(SupplierEntity supplierEntity);

}

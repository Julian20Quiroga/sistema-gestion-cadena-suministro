package com.logisync.sistema_cadena_suministro.application.mappers;

import com.logisync.sistema_cadena_suministro.application.commands.CreateSupplierCommand;
import com.logisync.sistema_cadena_suministro.application.responses.SupplierAppResponse;
import com.logisync.sistema_cadena_suministro.domain.models.Supplier;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SupplierApplicationMapper {

    Supplier toModel(CreateSupplierCommand command);
    SupplierAppResponse toResponse(Supplier supplier);
}

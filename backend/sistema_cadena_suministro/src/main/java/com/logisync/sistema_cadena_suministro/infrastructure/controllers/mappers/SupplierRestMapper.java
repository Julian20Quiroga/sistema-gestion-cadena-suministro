package com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers;

import com.logisync.sistema_cadena_suministro.application.commands.CreateSupplierCommand;
import com.logisync.sistema_cadena_suministro.application.responses.SupplierAppResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreateSupplierRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.SupplierResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SupplierRestMapper {

    CreateSupplierCommand toCommand(CreateSupplierRequest request);
    SupplierResponse toResponse(SupplierAppResponse response);
}

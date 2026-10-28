package com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers;

import com.logisync.sistema_cadena_suministro.application.commands.CreateSupplierCommand;
import com.logisync.sistema_cadena_suministro.application.commands.UpdateSupplierCommand;
import com.logisync.sistema_cadena_suministro.application.responses.SupplierAppResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreateSupplierRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.UpdateSupplierRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.SupplierResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SupplierRestMapper {

    CreateSupplierCommand toCommandCreate(CreateSupplierRequest request);
    UpdateSupplierCommand toCommandUpdate(UpdateSupplierRequest request);
    SupplierResponse toResponse(SupplierAppResponse response);
}

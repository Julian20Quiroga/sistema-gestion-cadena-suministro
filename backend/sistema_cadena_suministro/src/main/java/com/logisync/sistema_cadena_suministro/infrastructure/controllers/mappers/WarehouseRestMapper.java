package com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers;

import com.logisync.sistema_cadena_suministro.application.commands.CreateWarehouseCommand;
import com.logisync.sistema_cadena_suministro.application.commands.UpdateWarehouseCommand;
import com.logisync.sistema_cadena_suministro.application.responses.WarehouseAppResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreateWarehouseRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.UpdateWarehouseRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.WarehouseResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WarehouseRestMapper {

    CreateWarehouseCommand toCommandCreate(CreateWarehouseRequest request);

    UpdateWarehouseCommand toCommandUpdate(UpdateWarehouseRequest request);

    WarehouseResponse toResponse(WarehouseAppResponse response);
}

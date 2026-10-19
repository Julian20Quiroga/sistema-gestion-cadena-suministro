package com.logisync.sistema_cadena_suministro.application.mappers;

import com.logisync.sistema_cadena_suministro.application.commands.CreateWarehouseCommand;
import com.logisync.sistema_cadena_suministro.application.commands.UpdateWarehouseCommand;
import com.logisync.sistema_cadena_suministro.application.responses.WarehouseAppResponse;
import com.logisync.sistema_cadena_suministro.domain.models.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WarehouseApplicationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Warehouse toModelCreate(CreateWarehouseCommand command);

    @Mapping(target = "id", ignore = true)
    Warehouse toModelUpdate(UpdateWarehouseCommand command);

    WarehouseAppResponse toResponse(Warehouse warehouse);
}

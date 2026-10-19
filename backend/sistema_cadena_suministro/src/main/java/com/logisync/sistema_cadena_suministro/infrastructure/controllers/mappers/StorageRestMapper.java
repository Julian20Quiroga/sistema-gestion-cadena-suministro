package com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers;

import com.logisync.sistema_cadena_suministro.application.commands.CreateStorageSpaceCommand;
import com.logisync.sistema_cadena_suministro.application.commands.UpdateStorageSpaceCommand;
import com.logisync.sistema_cadena_suministro.application.responses.FreeLocationAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.StorageNodeAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.StorageSpaceAppResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreateStorageSpaceRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.UpdateStorageSpaceRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.FreeLocationResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.StorageNodeResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.StorageSpaceResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StorageRestMapper {

    CreateStorageSpaceCommand toCommandCreate(CreateStorageSpaceRequest request);

    UpdateStorageSpaceCommand toCommandUpdate(UpdateStorageSpaceRequest request);

    StorageSpaceResponse toResponse(StorageSpaceAppResponse response);

    StorageNodeResponse toResponse(StorageNodeAppResponse response);

    FreeLocationResponse toResponse(FreeLocationAppResponse response);
}

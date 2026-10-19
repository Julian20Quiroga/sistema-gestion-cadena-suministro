package com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers;

import com.logisync.sistema_cadena_suministro.application.commands.CreateDispatchCommand;
import com.logisync.sistema_cadena_suministro.application.responses.DetailDispatchAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.DispatchAppResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreateDispatchRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.DetailDispatchResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.DispatchResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DispatchRestMapper {
    CreateDispatchCommand toCommand(CreateDispatchRequest request);
    DispatchResponse toResponse(DispatchAppResponse response);
    DetailDispatchResponse toDetailResponse(DetailDispatchAppResponse response);
}

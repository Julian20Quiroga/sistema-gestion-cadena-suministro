package com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers;

import com.logisync.sistema_cadena_suministro.application.commands.CreateVehicleCommand;
import com.logisync.sistema_cadena_suministro.application.commands.OptimizeRouteCommand;
import com.logisync.sistema_cadena_suministro.application.responses.OptimizedRouteAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.VehicleAppResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreateVehicleRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.OptimizeRouteRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.OptimizedRouteResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.VehicleResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VehicleRestMapper {

    CreateVehicleCommand toCommand(CreateVehicleRequest request);

    OptimizeRouteCommand toRouteCommand(OptimizeRouteRequest request);

    VehicleResponse toResponse(VehicleAppResponse response);

    OptimizedRouteResponse toRouteResponse(OptimizedRouteAppResponse response);
}

package com.logisync.sistema_cadena_suministro.application.mappers;

import com.logisync.sistema_cadena_suministro.application.responses.OptimizedRouteAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.VehicleAppResponse;
import com.logisync.sistema_cadena_suministro.domain.models.Vehicle;
import com.logisync.sistema_cadena_suministro.domain.routes.OptimizedRoute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VehicleApplicationMapper {

    @Mapping(target = "rawApiKey", ignore = true)
    VehicleAppResponse toResponse(Vehicle vehicle);

    default VehicleAppResponse toResponseWithKey(Vehicle vehicle, String rawApiKey) {
        return new VehicleAppResponse(
                vehicle.getId(),
                vehicle.getPlate(),
                vehicle.getModel(),
                vehicle.getCapacityKg(),
                vehicle.getAverageSpeedKmh(),
                vehicle.getStatus(),
                rawApiKey
        );
    }

    OptimizedRouteAppResponse toRouteResponse(OptimizedRoute route);
}

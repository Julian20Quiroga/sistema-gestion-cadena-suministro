package com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers;

import com.logisync.sistema_cadena_suministro.application.commands.IngestTelemetryCommand;
import com.logisync.sistema_cadena_suministro.application.responses.TelemetryReadingAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.VehicleTrailAppResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.IngestTelemetryRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.TelemetryReadingResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.VehicleTrailResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrackingRestMapper {
    IngestTelemetryCommand toCommand(IngestTelemetryRequest request);
    TelemetryReadingResponse toResponse(TelemetryReadingAppResponse appResponse);
    VehicleTrailResponse toResponse(VehicleTrailAppResponse appResponse);
}

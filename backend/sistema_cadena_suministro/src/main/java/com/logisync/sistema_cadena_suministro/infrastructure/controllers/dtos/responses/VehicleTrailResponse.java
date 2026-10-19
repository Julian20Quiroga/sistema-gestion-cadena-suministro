package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

import java.util.List;

public record VehicleTrailResponse(
        Integer vehicleId,
        String vehiclePlate,
        String geoJsonLineString,
        List<TelemetryReadingResponse> readings
) {}

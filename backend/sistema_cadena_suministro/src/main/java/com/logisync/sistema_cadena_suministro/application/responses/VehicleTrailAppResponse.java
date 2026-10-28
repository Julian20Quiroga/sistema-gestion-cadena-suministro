package com.logisync.sistema_cadena_suministro.application.responses;

import java.util.List;

public record VehicleTrailAppResponse(
        Integer vehicleId,
        String vehiclePlate,
        String geoJsonLineString,
        List<TelemetryReadingAppResponse> readings
) {}

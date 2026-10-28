package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.responses.TelemetryReadingAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.VehicleTrailAppResponse;

public interface QueryTrackingUseCase {
    TelemetryReadingAppResponse getLatestByVehicleId(Integer vehicleId);
    TelemetryReadingAppResponse getLatestByDispatchId(Integer dispatchId);
    VehicleTrailAppResponse getTrailByVehicleId(Integer vehicleId, int limit);
}

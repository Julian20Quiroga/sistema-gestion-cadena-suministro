package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.responses.VehicleAppResponse;

public interface RotateVehicleApiKeyUseCase {
    VehicleAppResponse rotateApiKey(Integer vehicleId);
}

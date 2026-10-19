package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.CreateVehicleCommand;
import com.logisync.sistema_cadena_suministro.application.responses.VehicleAppResponse;

public interface CreateVehicleUseCase {
    VehicleAppResponse create(CreateVehicleCommand command);
}

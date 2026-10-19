package com.logisync.sistema_cadena_suministro.domain.tracking.chain;

import com.logisync.sistema_cadena_suministro.application.commands.IngestTelemetryCommand;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.models.Vehicle;

/**
 * Chain of Responsibility: Validates reported speed is physically plausible [0, 250 km/h].
 */
public class SpeedTelemetryValidator extends TelemetryValidationHandler {

    @Override
    public void validate(IngestTelemetryCommand command, Vehicle vehicle) {
        if (command.speedKmh() < 0) {
            throw new BusinessRuleException("Speed cannot be negative: " + command.speedKmh());
        }
        if (command.speedKmh() > 250.0) {
            throw new BusinessRuleException("Speed exceeds physical limit for logistics fleet (250 km/h): " + command.speedKmh());
        }
        checkNext(command, vehicle);
    }
}

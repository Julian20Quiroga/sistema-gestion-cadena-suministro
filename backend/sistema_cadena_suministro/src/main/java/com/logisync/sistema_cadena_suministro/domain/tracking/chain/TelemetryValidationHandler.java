package com.logisync.sistema_cadena_suministro.domain.tracking.chain;

import com.logisync.sistema_cadena_suministro.application.commands.IngestTelemetryCommand;
import com.logisync.sistema_cadena_suministro.domain.models.Vehicle;

/**
 * Chain of Responsibility: Abstract handler for validating inbound IoT telemetry readings.
 */
public abstract class TelemetryValidationHandler {

    private TelemetryValidationHandler next;

    public TelemetryValidationHandler linkWith(TelemetryValidationHandler next) {
        this.next = next;
        return next;
    }

    public abstract void validate(IngestTelemetryCommand command, Vehicle vehicle);

    protected void checkNext(IngestTelemetryCommand command, Vehicle vehicle) {
        if (next != null) {
            next.validate(command, vehicle);
        }
    }
}

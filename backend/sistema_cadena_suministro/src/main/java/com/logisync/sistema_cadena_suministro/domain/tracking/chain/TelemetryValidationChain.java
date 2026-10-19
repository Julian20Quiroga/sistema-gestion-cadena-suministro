package com.logisync.sistema_cadena_suministro.domain.tracking.chain;

import com.logisync.sistema_cadena_suministro.application.commands.IngestTelemetryCommand;
import com.logisync.sistema_cadena_suministro.domain.models.Vehicle;

/**
 * Chain of Responsibility: Builder and executor of the telemetry validation pipeline.
 */
public class TelemetryValidationChain {

    private final TelemetryValidationHandler head;

    public TelemetryValidationChain() {
        CoordinatesTelemetryValidator coordinatesValidator = new CoordinatesTelemetryValidator();
        TimestampTelemetryValidator timestampValidator = new TimestampTelemetryValidator();
        SpeedTelemetryValidator speedValidator = new SpeedTelemetryValidator();

        coordinatesValidator.linkWith(timestampValidator).linkWith(speedValidator);
        this.head = coordinatesValidator;
    }

    public void execute(IngestTelemetryCommand command, Vehicle vehicle) {
        head.validate(command, vehicle);
    }
}

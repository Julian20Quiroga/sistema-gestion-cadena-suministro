package com.logisync.sistema_cadena_suministro.domain.tracking.chain;

import com.logisync.sistema_cadena_suministro.application.commands.IngestTelemetryCommand;
import com.logisync.sistema_cadena_suministro.domain.models.Vehicle;

/**
 * Orquestador de la cadena de validación de telemetría IoT vehicular.
 * Patrón aplicado: Chain of Responsibility (ensambla y ejecuta secuencialmente los manejadores
 * de validación: coordenadas GPS válidas -> timestamp no futuro -> umbrales físicos de velocidad).
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

    /**
     * Ejecuta secuencialmente la cadena de validadores sobre la lectura de telemetría recibida.
     * Patrón aplicado: Chain of Responsibility (inicia la validación en la cabeza de la cadena).
     */
    public void execute(IngestTelemetryCommand command, Vehicle vehicle) {
        head.validate(command, vehicle);
    }
}

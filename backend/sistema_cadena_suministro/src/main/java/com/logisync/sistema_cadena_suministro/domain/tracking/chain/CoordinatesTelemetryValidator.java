package com.logisync.sistema_cadena_suministro.domain.tracking.chain;

import com.logisync.sistema_cadena_suministro.application.commands.IngestTelemetryCommand;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.models.Vehicle;

import java.math.BigDecimal;

/**
 * Chain of Responsibility: Validates GPS coordinates are within realistic global bounds (-90/90, -180/180).
 */
public class CoordinatesTelemetryValidator extends TelemetryValidationHandler {

    private static final BigDecimal MIN_LAT = new BigDecimal("-90.0");
    private static final BigDecimal MAX_LAT = new BigDecimal("90.0");
    private static final BigDecimal MIN_LON = new BigDecimal("-180.0");
    private static final BigDecimal MAX_LON = new BigDecimal("180.0");

    @Override
    public void validate(IngestTelemetryCommand command, Vehicle vehicle) {
        if (command.latitude() == null || command.longitude() == null) {
            throw new BusinessRuleException("GPS coordinates (latitude and longitude) are required");
        }
        if (command.latitude().compareTo(MIN_LAT) < 0 || command.latitude().compareTo(MAX_LAT) > 0) {
            throw new BusinessRuleException("Latitude " + command.latitude() + " is outside valid range [-90, 90]");
        }
        if (command.longitude().compareTo(MIN_LON) < 0 || command.longitude().compareTo(MAX_LON) > 0) {
            throw new BusinessRuleException("Longitude " + command.longitude() + " is outside valid range [-180, 180]");
        }
        checkNext(command, vehicle);
    }
}

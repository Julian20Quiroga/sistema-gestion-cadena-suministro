package com.logisync.sistema_cadena_suministro.domain.tracking.chain;

import com.logisync.sistema_cadena_suministro.application.commands.IngestTelemetryCommand;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.models.Vehicle;

import java.time.LocalDateTime;

/**
 * Chain of Responsibility: Validates recorded_at timestamp is not in future (> 5 min) or too old (> 7 days).
 */
public class TimestampTelemetryValidator extends TelemetryValidationHandler {

    @Override
    public void validate(IngestTelemetryCommand command, Vehicle vehicle) {
        if (command.recordedAt() == null) {
            throw new BusinessRuleException("Telemetry recorded timestamp is required");
        }
        LocalDateTime now = LocalDateTime.now();
        if (command.recordedAt().isAfter(now.plusMinutes(5))) {
            throw new BusinessRuleException("Telemetry timestamp cannot be in the future: " + command.recordedAt());
        }
        if (command.recordedAt().isBefore(now.minusDays(7))) {
            throw new BusinessRuleException("Telemetry timestamp cannot be older than 7 days: " + command.recordedAt());
        }
        checkNext(command, vehicle);
    }
}

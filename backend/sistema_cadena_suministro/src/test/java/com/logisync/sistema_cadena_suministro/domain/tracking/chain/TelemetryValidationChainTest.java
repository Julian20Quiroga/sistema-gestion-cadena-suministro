package com.logisync.sistema_cadena_suministro.domain.tracking.chain;

import com.logisync.sistema_cadena_suministro.application.commands.IngestTelemetryCommand;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.models.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TelemetryValidationChainTest {

    private TelemetryValidationChain chain;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        chain = new TelemetryValidationChain();
        vehicle = new Vehicle();
        vehicle.setId(1);
    }

    @Test
    void validTelemetryPassesChain() {
        IngestTelemetryCommand command = new IngestTelemetryCommand(
                new BigDecimal("6.2442"),
                new BigDecimal("-75.5812"),
                45.0,
                LocalDateTime.now().minusMinutes(1)
        );
        assertDoesNotThrow(() -> chain.execute(command, vehicle));
    }

    @Test
    void invalidLatitudeThrowsException() {
        IngestTelemetryCommand command = new IngestTelemetryCommand(
                new BigDecimal("95.0"), // Outside [-90, 90]
                new BigDecimal("-75.5812"),
                45.0,
                LocalDateTime.now()
        );
        assertThrows(BusinessRuleException.class, () -> chain.execute(command, vehicle));
    }

    @Test
    void futureTimestampThrowsException() {
        IngestTelemetryCommand command = new IngestTelemetryCommand(
                new BigDecimal("6.2442"),
                new BigDecimal("-75.5812"),
                45.0,
                LocalDateTime.now().plusHours(2)
        );
        assertThrows(BusinessRuleException.class, () -> chain.execute(command, vehicle));
    }

    @Test
    void negativeOrExcessiveSpeedThrowsException() {
        IngestTelemetryCommand neg = new IngestTelemetryCommand(
                new BigDecimal("6.2442"),
                new BigDecimal("-75.5812"),
                -10.0,
                LocalDateTime.now()
        );
        assertThrows(BusinessRuleException.class, () -> chain.execute(neg, vehicle));

        IngestTelemetryCommand tooFast = new IngestTelemetryCommand(
                new BigDecimal("6.2442"),
                new BigDecimal("-75.5812"),
                320.0,
                LocalDateTime.now()
        );
        assertThrows(BusinessRuleException.class, () -> chain.execute(tooFast, vehicle));
    }
}

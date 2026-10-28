package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.application.commands.CreateVehicleCommand;
import com.logisync.sistema_cadena_suministro.application.commands.IngestTelemetryCommand;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreateVehicleUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.IngestTelemetryUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryTrackingUseCase;
import com.logisync.sistema_cadena_suministro.application.responses.VehicleAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.TelemetryReadingAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.VehicleTrailAppResponse;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ConflictException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.UnauthorizedException;
import com.logisync.sistema_cadena_suministro.infrastructure.bridge.telemetry.StreamTelemetrySink;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TrackingIntegrationTest {

    @Autowired
    private CreateVehicleUseCase createVehicleUseCase;

    @Autowired
    private IngestTelemetryUseCase ingestTelemetryUseCase;

    @Autowired
    private QueryTrackingUseCase queryTrackingUseCase;

    @Autowired
    private StreamTelemetrySink streamTelemetrySink;

    @Test
    void ingestTelemetryStoresDataAndUpdatesBridgeSink() {
        String plate = "TRK-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        VehicleAppResponse vehicle = createVehicleUseCase.create(new CreateVehicleCommand(
                plate, "Volvo FH", new BigDecimal("12000"), new BigDecimal("45.0")
        ));

        LocalDateTime t1 = LocalDateTime.now().minusMinutes(2);
        IngestTelemetryCommand cmd1 = new IngestTelemetryCommand(
                new BigDecimal("6.244200"),
                new BigDecimal("-75.581200"),
                60.0,
                t1
        );

        TelemetryReadingAppResponse r1 = ingestTelemetryUseCase.ingest(vehicle.rawApiKey(), cmd1);
        assertNotNull(r1.id());
        assertEquals(vehicle.id(), r1.vehicleId());
        assertEquals(plate, r1.vehiclePlate());
        assertTrue(streamTelemetrySink.getBufferSize() > 0);

        // Ingest second reading
        LocalDateTime t2 = LocalDateTime.now().minusMinutes(1);
        IngestTelemetryCommand cmd2 = new IngestTelemetryCommand(
                new BigDecimal("6.250000"),
                new BigDecimal("-75.570000"),
                110.0, // High speed -> triggers UrgentAlertTelemetryPublication
                t2
        );
        ingestTelemetryUseCase.ingest(vehicle.rawApiKey(), cmd2);

        // Query latest
        TelemetryReadingAppResponse latest = queryTrackingUseCase.getLatestByVehicleId(vehicle.id());
        assertEquals(new BigDecimal("6.250000"), latest.latitude());
        assertEquals(110.0, latest.speedKmh());

        // Query trail
        VehicleTrailAppResponse trail = queryTrackingUseCase.getTrailByVehicleId(vehicle.id(), 10);
        assertEquals(2, trail.readings().size());
        assertTrue(trail.geoJsonLineString().contains("LineString"));

        // Duplicate recorded_at throws ConflictException
        assertThrows(ConflictException.class, () -> ingestTelemetryUseCase.ingest(vehicle.rawApiKey(), cmd2));

        // Invalid API Key throws UnauthorizedException
        assertThrows(UnauthorizedException.class, () -> ingestTelemetryUseCase.ingest("INVALID_KEY", cmd1));
    }
}

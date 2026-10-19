package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.IngestTelemetryCommand;
import com.logisync.sistema_cadena_suministro.application.responses.TelemetryReadingAppResponse;

public interface IngestTelemetryUseCase {
    TelemetryReadingAppResponse ingest(String apiKey, IngestTelemetryCommand command);
}

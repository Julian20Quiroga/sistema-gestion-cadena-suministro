package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.GenerateForecastCommand;
import com.logisync.sistema_cadena_suministro.application.responses.ForecastBatchSummaryAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.ProductForecastAppResponse;

public interface GenerateForecastUseCase {
    ForecastBatchSummaryAppResponse generate(GenerateForecastCommand command);
    ProductForecastAppResponse getForecastForProduct(Integer productId, String algorithm, int periodsAhead);
}

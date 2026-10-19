package com.logisync.sistema_cadena_suministro.application.responses;

import java.util.List;

public record ForecastBatchSummaryAppResponse(
        int productsProcessed,
        int supplyNeedsCreated,
        List<ProductForecastAppResponse> forecasts
) {}

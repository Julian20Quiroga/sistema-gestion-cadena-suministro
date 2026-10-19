package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

import java.util.List;

public record ForecastBatchSummaryResponse(
        int productsProcessed,
        int supplyNeedsCreated,
        List<ProductForecastResponse> forecasts
) {}

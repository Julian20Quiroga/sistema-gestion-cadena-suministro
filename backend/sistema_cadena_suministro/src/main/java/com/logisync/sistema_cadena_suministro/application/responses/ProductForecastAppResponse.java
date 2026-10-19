package com.logisync.sistema_cadena_suministro.application.responses;

import java.util.List;

public record ProductForecastAppResponse(
        Integer productId,
        String productName,
        String sku,
        String algorithm,
        List<Double> historicalPoints,
        List<Double> projections,
        double totalProjectedDemand,
        int currentAvailableStock,
        int recommendedProcurementQuantity,
        boolean supplyNeedCreated,
        Integer supplyNeedId
) {}

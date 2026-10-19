package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

import java.util.List;

public record ProductForecastResponse(
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

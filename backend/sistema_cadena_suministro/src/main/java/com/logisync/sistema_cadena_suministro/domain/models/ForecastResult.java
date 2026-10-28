package com.logisync.sistema_cadena_suministro.domain.models;

import java.util.List;

public record ForecastResult(
        String algorithmName,
        List<Double> projectedQuantities,
        double recommendedProcurementQuantity
) {}

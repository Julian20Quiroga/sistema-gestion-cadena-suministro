package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

public record GenerateForecastRequest(
        Integer productId,
        String algorithm,
        @Min(value = 1, message = "Periods ahead must be at least 1")
        Integer periodsAhead,
        @DecimalMin(value = "0.1", message = "Seasonal multiplier must be positive")
        Double seasonalMultiplier,
        @DecimalMin(value = "0.0", message = "Safety stock buffer cannot be negative")
        Double safetyStockBufferPercentage,
        Boolean autoCreateSupplyNeeds
) {}

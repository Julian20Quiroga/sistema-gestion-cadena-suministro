package com.logisync.sistema_cadena_suministro.application.commands;

public record GenerateForecastCommand(
        Integer productId,
        String algorithm,
        int periodsAhead,
        Double seasonalMultiplier,
        Double safetyStockBufferPercentage,
        boolean autoCreateSupplyNeeds
) {
    public GenerateForecastCommand {
        if (periodsAhead <= 0) periodsAhead = 3;
    }
}

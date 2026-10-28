package com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers;

import com.logisync.sistema_cadena_suministro.application.commands.GenerateForecastCommand;
import com.logisync.sistema_cadena_suministro.application.responses.ForecastBatchSummaryAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.ProductForecastAppResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.GenerateForecastRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.ForecastBatchSummaryResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.ProductForecastResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ForecastRestMapper {
    default GenerateForecastCommand toCommand(GenerateForecastRequest req) {
        int periods = (req.periodsAhead() != null) ? req.periodsAhead() : 3;
        boolean auto = (req.autoCreateSupplyNeeds() != null) ? req.autoCreateSupplyNeeds() : true;
        return new GenerateForecastCommand(
                req.productId(),
                req.algorithm(),
                periods,
                req.seasonalMultiplier(),
                req.safetyStockBufferPercentage(),
                auto
        );
    }

    ProductForecastResponse toResponse(ProductForecastAppResponse app);
    ForecastBatchSummaryResponse toResponse(ForecastBatchSummaryAppResponse app);
}

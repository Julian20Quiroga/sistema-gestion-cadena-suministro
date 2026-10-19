package com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers;

import com.logisync.sistema_cadena_suministro.application.commands.RegisterAdjustmentCommand;
import com.logisync.sistema_cadena_suministro.application.commands.RegisterEntryCommand;
import com.logisync.sistema_cadena_suministro.application.commands.RegisterExitCommand;
import com.logisync.sistema_cadena_suministro.application.commands.RegisterTransferCommand;
import com.logisync.sistema_cadena_suministro.application.responses.InventoryLevelAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.MovementAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.ProductAvailabilityAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.ProductRotationAppResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.RegisterAdjustmentRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.RegisterEntryRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.RegisterExitRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.RegisterTransferRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.InventoryLevelResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.MovementResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.ProductAvailabilityResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.ProductRotationResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryRestMapper {

    RegisterEntryCommand toCommand(RegisterEntryRequest request);

    RegisterExitCommand toCommand(RegisterExitRequest request);

    RegisterAdjustmentCommand toCommand(RegisterAdjustmentRequest request);

    RegisterTransferCommand toCommand(RegisterTransferRequest request);

    MovementResponse toResponse(MovementAppResponse response);

    InventoryLevelResponse toResponse(InventoryLevelAppResponse response);

    ProductAvailabilityResponse toResponse(ProductAvailabilityAppResponse response);

    ProductRotationResponse toResponse(ProductRotationAppResponse response);
}

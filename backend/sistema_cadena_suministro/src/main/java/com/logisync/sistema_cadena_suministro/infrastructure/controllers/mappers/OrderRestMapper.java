package com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers;

import com.logisync.sistema_cadena_suministro.application.commands.CreateOrderCommand;
import com.logisync.sistema_cadena_suministro.application.responses.OrderAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.OrderDetailAppResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreateOrderRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.OrderDetailResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.OrderResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderRestMapper {

    CreateOrderCommand toCommand(CreateOrderRequest request);

    OrderResponse toResponse(OrderAppResponse response);

    OrderDetailResponse toDetailResponse(OrderDetailAppResponse response);
}

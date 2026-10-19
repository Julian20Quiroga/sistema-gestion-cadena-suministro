package com.logisync.sistema_cadena_suministro.application.mappers;

import com.logisync.sistema_cadena_suministro.application.responses.OrderAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.OrderDetailAppResponse;
import com.logisync.sistema_cadena_suministro.domain.models.Order;
import com.logisync.sistema_cadena_suministro.domain.models.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderApplicationMapper {

    @Mapping(target = "totalAmount", expression = "java(order.totalAmount())")
    OrderAppResponse toResponse(Order order);

    @Mapping(target = "subtotal", expression = "java(detail.subtotal())")
    OrderDetailAppResponse toDetailResponse(OrderDetail detail);
}

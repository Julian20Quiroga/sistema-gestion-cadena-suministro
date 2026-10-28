package com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers;

import com.logisync.sistema_cadena_suministro.domain.models.Order;
import com.logisync.sistema_cadena_suministro.domain.models.OrderDetail;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.OrderDetailEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderPersistenceMapper {

    @Mapping(target = "details", source = "details")
    Order toDomain(OrderEntity entity);

    @Mapping(target = "orderId", source = "order.id")
    OrderDetail toDetailDomain(OrderDetailEntity entity);

    @Mapping(target = "details", ignore = true)
    OrderEntity toEntity(Order domain);
}

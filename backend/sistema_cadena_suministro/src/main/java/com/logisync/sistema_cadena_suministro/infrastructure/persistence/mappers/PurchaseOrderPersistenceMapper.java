package com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers;

import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrder;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrderDetail;
import com.logisync.sistema_cadena_suministro.domain.models.SupplyNeed;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.PurchaseOrderDetailEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.PurchaseOrderEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.SupplyNeedEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PurchaseOrderPersistenceMapper {

    @Mapping(target = "details", source = "details")
    PurchaseOrder toDomain(PurchaseOrderEntity entity);

    @Mapping(target = "purchaseOrderId", source = "purchaseOrder.id")
    PurchaseOrderDetail toDetailDomain(PurchaseOrderDetailEntity entity);

    @Mapping(target = "details", ignore = true)
    PurchaseOrderEntity toEntity(PurchaseOrder domain);

    @Mapping(target = "createdAt", source = "createdDate")
    @Mapping(target = "updatedAt", source = "lastModifiedDate")
    SupplyNeed toDomain(SupplyNeedEntity entity);

    SupplyNeedEntity toEntity(SupplyNeed domain);
}

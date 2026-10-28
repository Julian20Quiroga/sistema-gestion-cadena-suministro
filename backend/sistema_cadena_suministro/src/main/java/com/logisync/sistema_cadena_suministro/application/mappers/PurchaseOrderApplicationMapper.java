package com.logisync.sistema_cadena_suministro.application.mappers;

import com.logisync.sistema_cadena_suministro.application.responses.PurchaseOrderAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.PurchaseOrderDetailAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.SupplyNeedAppResponse;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrder;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrderDetail;
import com.logisync.sistema_cadena_suministro.domain.models.SupplyNeed;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PurchaseOrderApplicationMapper {

    PurchaseOrderAppResponse toResponse(PurchaseOrder order);

    PurchaseOrderDetailAppResponse toDetailResponse(PurchaseOrderDetail detail);

    SupplyNeedAppResponse toSupplyNeedResponse(SupplyNeed need);
}

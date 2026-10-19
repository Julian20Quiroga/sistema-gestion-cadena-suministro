package com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers;

import com.logisync.sistema_cadena_suministro.application.commands.CreatePurchaseOrderCommand;
import com.logisync.sistema_cadena_suministro.application.commands.ReceivePurchaseOrderCommand;
import com.logisync.sistema_cadena_suministro.application.responses.PurchaseOrderAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.PurchaseOrderDetailAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.SupplyNeedAppResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreatePurchaseOrderRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.ReceivePurchaseOrderRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.PurchaseOrderDetailResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.PurchaseOrderResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.SupplyNeedResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PurchaseOrderRestMapper {

    CreatePurchaseOrderCommand toCommand(CreatePurchaseOrderRequest request);

    default ReceivePurchaseOrderCommand toReceiveCommand(Integer poId, ReceivePurchaseOrderRequest request) {
        return new ReceivePurchaseOrderCommand(
                poId,
                request.items().stream()
                        .map(i -> new ReceivePurchaseOrderCommand.ReceptionDetail(i.productId(), i.storageSpaceId(), i.quantity()))
                        .toList()
        );
    }

    PurchaseOrderResponse toResponse(PurchaseOrderAppResponse response);

    PurchaseOrderDetailResponse toDetailResponse(PurchaseOrderDetailAppResponse response);

    SupplyNeedResponse toSupplyNeedResponse(SupplyNeedAppResponse response);
}

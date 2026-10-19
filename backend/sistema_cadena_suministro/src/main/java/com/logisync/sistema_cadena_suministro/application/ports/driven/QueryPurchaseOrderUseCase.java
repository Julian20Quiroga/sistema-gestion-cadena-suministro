package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.responses.PurchaseOrderAppResponse;
import com.logisync.sistema_cadena_suministro.domain.enums.PurchaseOrderStatus;

public interface QueryPurchaseOrderUseCase {
    PurchaseOrderAppResponse findById(Integer id);
    PageResult<PurchaseOrderAppResponse> findAll(Integer supplierId, Integer warehouseId,
                                                PurchaseOrderStatus status, PageQuery pageQuery);
}

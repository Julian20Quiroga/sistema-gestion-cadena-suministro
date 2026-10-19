package com.logisync.sistema_cadena_suministro.application.ports.driving;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.domain.enums.PurchaseOrderStatus;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrder;

import java.util.Optional;

public interface PurchaseOrderRepository {
    PurchaseOrder save(PurchaseOrder order);
    Optional<PurchaseOrder> findById(Integer id);
    PageResult<PurchaseOrder> findAll(Integer supplierId, Integer warehouseId,
                                     PurchaseOrderStatus status, PageQuery pageQuery);
}

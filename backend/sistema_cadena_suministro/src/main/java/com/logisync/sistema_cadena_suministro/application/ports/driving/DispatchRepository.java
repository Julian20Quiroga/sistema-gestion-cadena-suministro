package com.logisync.sistema_cadena_suministro.application.ports.driving;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.domain.enums.DispatchStatus;
import com.logisync.sistema_cadena_suministro.domain.models.Dispatch;

import java.util.Optional;

public interface DispatchRepository {
    Dispatch save(Dispatch dispatch);
    Optional<Dispatch> findById(Integer id);
    boolean existsByOrderId(Integer orderId);
    PageResult<Dispatch> findAll(Integer warehouseId, Integer orderId, DispatchStatus status, PageQuery pageQuery);
}

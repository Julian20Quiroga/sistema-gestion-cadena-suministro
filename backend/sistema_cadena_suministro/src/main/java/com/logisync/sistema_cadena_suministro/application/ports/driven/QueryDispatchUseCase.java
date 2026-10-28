package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.responses.DispatchAppResponse;
import com.logisync.sistema_cadena_suministro.domain.enums.DispatchStatus;

public interface QueryDispatchUseCase {
    DispatchAppResponse findById(Integer id);
    PageResult<DispatchAppResponse> findAll(Integer warehouseId, Integer orderId, DispatchStatus status, PageQuery pageQuery);
}

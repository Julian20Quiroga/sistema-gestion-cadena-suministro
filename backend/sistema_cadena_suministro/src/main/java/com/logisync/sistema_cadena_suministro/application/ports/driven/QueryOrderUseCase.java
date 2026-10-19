package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.responses.OrderAppResponse;
import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;

public interface QueryOrderUseCase {
    OrderAppResponse findById(Integer id);
    OrderAppResponse findByReference(String reference);
    PageResult<OrderAppResponse> findAll(String reference, OrderStatus status, PageQuery pageQuery);
}

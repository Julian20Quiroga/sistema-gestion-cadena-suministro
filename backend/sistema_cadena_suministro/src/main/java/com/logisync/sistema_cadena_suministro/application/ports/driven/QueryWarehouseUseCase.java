package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.responses.WarehouseAppResponse;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;

public interface QueryWarehouseUseCase {
    WarehouseAppResponse findById(Integer id);

    PageResult<WarehouseAppResponse> findAll(String name, SoftDeleteStatus status, PageQuery pageQuery);
}

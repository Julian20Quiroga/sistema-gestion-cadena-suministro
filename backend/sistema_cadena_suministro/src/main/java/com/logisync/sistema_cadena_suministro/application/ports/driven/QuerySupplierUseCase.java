package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.responses.SupplierAppResponse;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;

public interface QuerySupplierUseCase {
    SupplierAppResponse findById(Integer id);

    PageResult<SupplierAppResponse> findAll(String name, SoftDeleteStatus status, PageQuery pageQuery);
}

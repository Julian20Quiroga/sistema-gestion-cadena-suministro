package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.responses.SupplyNeedAppResponse;
import com.logisync.sistema_cadena_suministro.domain.enums.SupplyNeedStatus;

public interface ManageSupplyNeedUseCase {
    PageResult<SupplyNeedAppResponse> findAll(SupplyNeedStatus status, PageQuery pageQuery);
    SupplyNeedAppResponse dismiss(Integer id);
}

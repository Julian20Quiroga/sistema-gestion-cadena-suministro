package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.queries.MovementFilter;
import com.logisync.sistema_cadena_suministro.application.responses.MovementAppResponse;

public interface QueryMovementUseCase {
    PageResult<MovementAppResponse> findMovements(MovementFilter filter, PageQuery pageQuery);
}

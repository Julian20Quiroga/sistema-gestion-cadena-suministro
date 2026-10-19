package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.queries.ProductFilter;
import com.logisync.sistema_cadena_suministro.application.responses.ProductAppResponse;

public interface QueryProductUseCase {
    ProductAppResponse findById(Integer id);

    PageResult<ProductAppResponse> findAll(ProductFilter filter, PageQuery pageQuery);
}

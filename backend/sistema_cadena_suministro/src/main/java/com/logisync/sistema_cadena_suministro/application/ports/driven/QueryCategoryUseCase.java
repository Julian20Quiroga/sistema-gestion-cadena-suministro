package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.responses.CategoryAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.CategoryGroupAppResponse;

import java.util.List;

public interface QueryCategoryUseCase {
    List<CategoryGroupAppResponse> findGroups();

    List<CategoryAppResponse> findCategories(Integer groupId);
}

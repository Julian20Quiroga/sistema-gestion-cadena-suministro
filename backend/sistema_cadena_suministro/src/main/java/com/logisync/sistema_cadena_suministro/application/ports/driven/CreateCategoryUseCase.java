package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.CreateCategoryCommand;
import com.logisync.sistema_cadena_suministro.application.commands.CreateCategoryGroupCommand;
import com.logisync.sistema_cadena_suministro.application.responses.CategoryAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.CategoryGroupAppResponse;

public interface CreateCategoryUseCase {
    CategoryGroupAppResponse createGroup(CreateCategoryGroupCommand command);

    CategoryAppResponse createCategory(CreateCategoryCommand command);
}

package com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers;

import com.logisync.sistema_cadena_suministro.application.commands.CreateCategoryCommand;
import com.logisync.sistema_cadena_suministro.application.commands.CreateCategoryGroupCommand;
import com.logisync.sistema_cadena_suministro.application.responses.CategoryAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.CategoryGroupAppResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreateCategoryGroupRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreateCategoryRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.CategoryGroupResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.CategoryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryRestMapper {

    CreateCategoryGroupCommand toCommand(CreateCategoryGroupRequest request);

    CreateCategoryCommand toCommand(CreateCategoryRequest request);

    CategoryResponse toResponse(CategoryAppResponse response);

    CategoryGroupResponse toResponse(CategoryGroupAppResponse response);
}

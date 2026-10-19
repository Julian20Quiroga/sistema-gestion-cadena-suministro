package com.logisync.sistema_cadena_suministro.application.mappers;

import com.logisync.sistema_cadena_suministro.application.commands.CreateCategoryCommand;
import com.logisync.sistema_cadena_suministro.application.commands.CreateCategoryGroupCommand;
import com.logisync.sistema_cadena_suministro.application.responses.CategoryAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.CategoryGroupAppResponse;
import com.logisync.sistema_cadena_suministro.domain.models.Category;
import com.logisync.sistema_cadena_suministro.domain.models.CategoryGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryApplicationMapper {

    @Mapping(target = "id", ignore = true)
    CategoryGroup toModel(CreateCategoryGroupCommand command);

    @Mapping(target = "id", ignore = true)
    Category toModel(CreateCategoryCommand command);

    CategoryAppResponse toResponse(Category category);

    CategoryGroupAppResponse toResponse(CategoryGroup group);
}

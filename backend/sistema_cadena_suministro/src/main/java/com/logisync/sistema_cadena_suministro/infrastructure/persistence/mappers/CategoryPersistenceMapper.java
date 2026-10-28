package com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers;

import com.logisync.sistema_cadena_suministro.domain.models.Category;
import com.logisync.sistema_cadena_suministro.domain.models.CategoryGroup;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.CategoryEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.CategoryGroupEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryPersistenceMapper {

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    CategoryEntity toEntity(Category category);

    Category toDomain(CategoryEntity entity);

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    CategoryGroupEntity toEntity(CategoryGroup group);

    CategoryGroup toDomain(CategoryGroupEntity entity);
}

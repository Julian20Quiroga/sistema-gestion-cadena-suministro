package com.logisync.sistema_cadena_suministro.application.ports.driving;

import com.logisync.sistema_cadena_suministro.domain.models.Category;
import com.logisync.sistema_cadena_suministro.domain.models.CategoryGroup;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    CategoryGroup createGroup(CategoryGroup group);
    Category createCategory(Category category);
    Optional<CategoryGroup> findGroupById(Integer id);
    List<CategoryGroup> findGroups();
    List<Category> findCategories(Integer groupId);
    List<Category> findCategoriesByIds(Collection<Integer> ids);
}

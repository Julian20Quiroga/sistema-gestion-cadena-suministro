package com.logisync.sistema_cadena_suministro.infrastructure.persistence.adapters;

import com.logisync.sistema_cadena_suministro.application.ports.driving.CategoryRepository;
import com.logisync.sistema_cadena_suministro.domain.models.Category;
import com.logisync.sistema_cadena_suministro.domain.models.CategoryGroup;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.CategoryDAO;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.CategoryGroupDAO;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers.CategoryPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryAdapterPersistence implements CategoryRepository {

    private final CategoryDAO categoryDAO;
    private final CategoryGroupDAO categoryGroupDAO;
    private final CategoryPersistenceMapper mapper;

    @Override
    public CategoryGroup createGroup(CategoryGroup group) {
        return mapper.toDomain(categoryGroupDAO.save(mapper.toEntity(group)));
    }

    @Override
    public Category createCategory(Category category) {
        return mapper.toDomain(categoryDAO.save(mapper.toEntity(category)));
    }

    @Override
    public Optional<CategoryGroup> findGroupById(Integer id) {
        return categoryGroupDAO.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<CategoryGroup> findGroups() {
        return categoryGroupDAO.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Category> findCategories(Integer groupId) {
        var entities = groupId == null ? categoryDAO.findAll() : categoryDAO.findByGroupId(groupId);
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Category> findCategoriesByIds(Collection<Integer> ids) {
        return categoryDAO.findAllById(ids).stream().map(mapper::toDomain).toList();
    }
}

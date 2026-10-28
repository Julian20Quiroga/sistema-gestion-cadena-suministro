package com.logisync.sistema_cadena_suministro.application.service;

import com.logisync.sistema_cadena_suministro.application.commands.CreateCategoryCommand;
import com.logisync.sistema_cadena_suministro.application.commands.CreateCategoryGroupCommand;
import com.logisync.sistema_cadena_suministro.application.mappers.CategoryApplicationMapper;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreateCategoryUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryCategoryUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.CategoryRepository;
import com.logisync.sistema_cadena_suministro.application.responses.CategoryAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.CategoryGroupAppResponse;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements CreateCategoryUseCase, QueryCategoryUseCase {

    private final CategoryApplicationMapper mapper;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryGroupAppResponse createGroup(CreateCategoryGroupCommand command) {
        return mapper.toResponse(categoryRepository.createGroup(mapper.toModel(command)));
    }

    @Override
    @Transactional
    public CategoryAppResponse createCategory(CreateCategoryCommand command) {
        categoryRepository.findGroupById(command.groupId())
                .orElseThrow(() -> new ResourceNotFoundException("Category group", command.groupId()));
        return mapper.toResponse(categoryRepository.createCategory(mapper.toModel(command)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryGroupAppResponse> findGroups() {
        return categoryRepository.findGroups().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryAppResponse> findCategories(Integer groupId) {
        return categoryRepository.findCategories(groupId).stream().map(mapper::toResponse).toList();
    }
}

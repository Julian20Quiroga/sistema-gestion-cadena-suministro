package com.logisync.sistema_cadena_suministro.infrastructure.controllers;

import com.logisync.sistema_cadena_suministro.application.ports.driven.CreateCategoryUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryCategoryUseCase;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreateCategoryGroupRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreateCategoryRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.CategoryGroupResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.CategoryResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers.CategoryRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRestMapper restMapper;
    private final CreateCategoryUseCase createCategoryUseCase;
    private final QueryCategoryUseCase queryCategoryUseCase;

    @PreAuthorize("hasRole('PRESIDENT')")
    @PostMapping("/category-groups")
    public ResponseEntity<CategoryGroupResponse> createGroup(@Valid @RequestBody CreateCategoryGroupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(restMapper.toResponse(createCategoryUseCase.createGroup(restMapper.toCommand(request))));
    }

    @PreAuthorize("hasRole('PRESIDENT')")
    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(restMapper.toResponse(createCategoryUseCase.createCategory(restMapper.toCommand(request))));
    }

    @PreAuthorize("hasAnyRole('PRESIDENT','BRANCH_ADMIN')")
    @GetMapping("/category-groups")
    public ResponseEntity<List<CategoryGroupResponse>> findGroups() {
        return ResponseEntity.ok(queryCategoryUseCase.findGroups().stream().map(restMapper::toResponse).toList());
    }

    @PreAuthorize("hasAnyRole('PRESIDENT','BRANCH_ADMIN')")
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> findCategories(@RequestParam(required = false) Integer groupId) {
        return ResponseEntity.ok(queryCategoryUseCase.findCategories(groupId).stream().map(restMapper::toResponse).toList());
    }
}

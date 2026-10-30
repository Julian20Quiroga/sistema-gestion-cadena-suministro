package com.logisync.sistema_cadena_suministro.infrastructure.controllers;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreateWarehouseUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.DeleteWarehouseUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryWarehouseUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.UpdateWarehouseUseCase;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreateWarehouseRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.UpdateWarehouseRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.PageResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.WarehouseResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers.WarehouseRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseRestMapper restMapper;
    private final CreateWarehouseUseCase createWarehouseUseCase;
    private final UpdateWarehouseUseCase updateWarehouseUseCase;
    private final DeleteWarehouseUseCase deleteWarehouseUseCase;
    private final QueryWarehouseUseCase queryWarehouseUseCase;

    @PreAuthorize("hasRole('PRESIDENT')")
    @PostMapping
    public ResponseEntity<WarehouseResponse> create(@Valid @RequestBody CreateWarehouseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(restMapper.toResponse(createWarehouseUseCase.create(restMapper.toCommandCreate(request))));
    }

    @PreAuthorize("hasRole('PRESIDENT')")
    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponse> update(@Valid @RequestBody UpdateWarehouseRequest request,
            @PathVariable("id") Integer id) {
        return ResponseEntity.ok(restMapper.toResponse(
                updateWarehouseUseCase.update(restMapper.toCommandUpdate(request), id)));
    }

    @PreAuthorize("hasRole('PRESIDENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        deleteWarehouseUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('PRESIDENT','BRANCH_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponse> findById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(restMapper.toResponse(queryWarehouseUseCase.findById(id)));
    }

    @PreAuthorize("hasAnyRole('PRESIDENT','BRANCH_ADMIN')")
    @GetMapping
    public ResponseEntity<PageResponse<WarehouseResponse>> findAll(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "status", required = false) SoftDeleteStatus status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        return ResponseEntity.ok(PageResponse.from(
                queryWarehouseUseCase.findAll(name, status, PageQuery.of(page, size)), restMapper::toResponse));
    }
}


package com.logisync.sistema_cadena_suministro.infrastructure.controllers;

import com.logisync.sistema_cadena_suministro.application.commands.CreateSupplierCommand;
import com.logisync.sistema_cadena_suministro.application.commands.UpdateSupplierCommand;
import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreateSupplierUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.DeleteSupplierUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QuerySupplierUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.UpdateSupplierUseCase;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreateSupplierRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.UpdateSupplierRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.PageResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.SupplierResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers.SupplierRestMapper;
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
@RequestMapping("/api/supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierRestMapper supplierRestMapper;
    private final CreateSupplierUseCase createSupplierUseCase;
    private final UpdateSupplierUseCase updateSupplierUseCase;
    private final DeleteSupplierUseCase deleteSupplierUseCase;
    private final QuerySupplierUseCase querySupplierUseCase;

    @PreAuthorize("hasRole('PRESIDENT')")
    @PostMapping
    public ResponseEntity<SupplierResponse> create(@Valid @RequestBody CreateSupplierRequest request) {
        CreateSupplierCommand command = supplierRestMapper.toCommandCreate(request);
        SupplierResponse response = supplierRestMapper.toResponse(createSupplierUseCase.create(command));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('PRESIDENT')")
    @PutMapping ("/{id}")
    public ResponseEntity<SupplierResponse> update(@Valid @RequestBody UpdateSupplierRequest request, @PathVariable Integer id) {
        UpdateSupplierCommand command = supplierRestMapper.toCommandUpdate(request);
        SupplierResponse response = supplierRestMapper.toResponse(updateSupplierUseCase.update(command, id));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('PRESIDENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        deleteSupplierUseCase.delete(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('PRESIDENT','BRANCH_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(supplierRestMapper.toResponse(querySupplierUseCase.findById(id)));
    }

    @PreAuthorize("hasAnyRole('PRESIDENT','BRANCH_ADMIN')")
    @GetMapping
    public ResponseEntity<PageResponse<SupplierResponse>> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) SoftDeleteStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(PageResponse.from(
                querySupplierUseCase.findAll(name, status, PageQuery.of(page, size)),
                supplierRestMapper::toResponse));
    }

}

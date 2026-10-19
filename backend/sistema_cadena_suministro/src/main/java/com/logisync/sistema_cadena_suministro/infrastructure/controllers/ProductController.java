package com.logisync.sistema_cadena_suministro.infrastructure.controllers;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreateProductUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.DeleteProductUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryProductUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.UpdateProductUseCase;
import com.logisync.sistema_cadena_suministro.application.queries.ProductFilter;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreateProductRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.UpdateProductRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.PageResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.ProductResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers.ProductRestMapper;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRestMapper restMapper;
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final QueryProductUseCase queryProductUseCase;

    @PreAuthorize("hasRole('PRESIDENT')")
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = restMapper.toResponse(createProductUseCase.create(restMapper.toCommandCreate(request)));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('PRESIDENT')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@Valid @RequestBody UpdateProductRequest request,
            @PathVariable Integer id) {
        return ResponseEntity.ok(restMapper.toResponse(updateProductUseCase.update(restMapper.toCommandUpdate(request), id)));
    }

    @PreAuthorize("hasRole('PRESIDENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        deleteProductUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('PRESIDENT','BRANCH_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(restMapper.toResponse(queryProductUseCase.findById(id)));
    }

    @PreAuthorize("hasAnyRole('PRESIDENT','BRANCH_ADMIN')")
    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) Integer supplierId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) SoftDeleteStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        ProductFilter filter = new ProductFilter(name, sku, supplierId, categoryId, status);
        return ResponseEntity.ok(PageResponse.from(
                queryProductUseCase.findAll(filter, PageQuery.of(page, size)), restMapper::toResponse));
    }
}

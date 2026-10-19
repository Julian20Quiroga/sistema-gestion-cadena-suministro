package com.logisync.sistema_cadena_suministro.infrastructure.controllers;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CancelPurchaseOrderUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreatePurchaseOrderUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryPurchaseOrderUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.ReceivePurchaseOrderUseCase;
import com.logisync.sistema_cadena_suministro.domain.enums.PurchaseOrderStatus;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreatePurchaseOrderRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.ReceivePurchaseOrderRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.PageResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.PurchaseOrderResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers.PurchaseOrderRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/purchase-orders")
@Tag(name = "Purchase Orders", description = "Procurement and merchandise reception management")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final CreatePurchaseOrderUseCase createUseCase;
    private final ReceivePurchaseOrderUseCase receiveUseCase;
    private final CancelPurchaseOrderUseCase cancelUseCase;
    private final QueryPurchaseOrderUseCase queryUseCase;
    private final PurchaseOrderRestMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('PRESIDENT')")
    @Operation(summary = "Create purchase order")
    public ResponseEntity<PurchaseOrderResponse> create(@RequestBody @Valid CreatePurchaseOrderRequest request) {
        var res = createUseCase.create(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(res));
    }

    @PostMapping("/{id}/reception")
    @PreAuthorize("hasAnyRole('PRESIDENT', 'BRANCH_ADMIN')")
    @Operation(summary = "Receive merchandise for purchase order")
    public ResponseEntity<PurchaseOrderResponse> receive(
            @PathVariable("id") Integer id,
            @RequestBody @Valid ReceivePurchaseOrderRequest request) {
        var res = receiveUseCase.receive(mapper.toReceiveCommand(id, request));
        return ResponseEntity.ok(mapper.toResponse(res));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PRESIDENT')")
    @Operation(summary = "Cancel purchase order")
    public ResponseEntity<PurchaseOrderResponse> cancel(@PathVariable("id") Integer id) {
        var res = cancelUseCase.cancel(id);
        return ResponseEntity.ok(mapper.toResponse(res));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRESIDENT', 'BRANCH_ADMIN')")
    @Operation(summary = "Find purchase order by ID")
    public ResponseEntity<PurchaseOrderResponse> findById(@PathVariable("id") Integer id) {
        var res = queryUseCase.findById(id);
        return ResponseEntity.ok(mapper.toResponse(res));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PRESIDENT', 'BRANCH_ADMIN')")
    @Operation(summary = "Search purchase orders with pagination")
    public ResponseEntity<PageResponse<PurchaseOrderResponse>> findAll(
            @RequestParam(name = "supplierId", required = false) Integer supplierId,
            @RequestParam(name = "warehouseId", required = false) Integer warehouseId,
            @RequestParam(name = "status", required = false) PurchaseOrderStatus status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(PageResponse.from(
                queryUseCase.findAll(supplierId, warehouseId, status, PageQuery.of(page, size)),
                mapper::toResponse));
    }
}

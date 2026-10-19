package com.logisync.sistema_cadena_suministro.infrastructure.controllers;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreateDispatchUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.ManageDispatchStatusUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryDispatchUseCase;
import com.logisync.sistema_cadena_suministro.domain.enums.DispatchStatus;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreateDispatchRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.DispatchResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.PageResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers.DispatchRestMapper;
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
@RequestMapping("/api/dispatches")
@Tag(name = "Dispatches", description = "Order fulfillment, vehicle assignment and stock reservation")
@RequiredArgsConstructor
public class DispatchController {

    private final CreateDispatchUseCase createDispatchUseCase;
    private final ManageDispatchStatusUseCase manageDispatchStatusUseCase;
    private final QueryDispatchUseCase queryDispatchUseCase;
    private final DispatchRestMapper mapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('PRESIDENT', 'BRANCH_ADMIN')")
    @Operation(summary = "Create dispatch and reserve stock in warehouse")
    public ResponseEntity<DispatchResponse> create(@RequestBody @Valid CreateDispatchRequest request) {
        var res = createDispatchUseCase.create(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(res));
    }

    @PostMapping("/{id}/start-preparation")
    @PreAuthorize("hasAnyRole('PRESIDENT', 'BRANCH_ADMIN')")
    @Operation(summary = "Start dispatch picking/packing preparation")
    public ResponseEntity<DispatchResponse> startPreparation(@PathVariable("id") Integer id) {
        var res = manageDispatchStatusUseCase.startPreparation(id);
        return ResponseEntity.ok(mapper.toResponse(res));
    }

    @PostMapping("/{id}/ship")
    @PreAuthorize("hasAnyRole('PRESIDENT', 'BRANCH_ADMIN')")
    @Operation(summary = "Ship dispatch (consume reserved stock and start transit)")
    public ResponseEntity<DispatchResponse> ship(@PathVariable("id") Integer id) {
        var res = manageDispatchStatusUseCase.ship(id);
        return ResponseEntity.ok(mapper.toResponse(res));
    }

    @PostMapping("/{id}/deliver")
    @PreAuthorize("hasAnyRole('PRESIDENT', 'BRANCH_ADMIN')")
    @Operation(summary = "Mark dispatch as delivered")
    public ResponseEntity<DispatchResponse> deliver(@PathVariable("id") Integer id) {
        var res = manageDispatchStatusUseCase.deliver(id);
        return ResponseEntity.ok(mapper.toResponse(res));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('PRESIDENT', 'BRANCH_ADMIN')")
    @Operation(summary = "Cancel dispatch and release reserved stock")
    public ResponseEntity<DispatchResponse> cancel(@PathVariable("id") Integer id) {
        var res = manageDispatchStatusUseCase.cancel(id);
        return ResponseEntity.ok(mapper.toResponse(res));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRESIDENT', 'BRANCH_ADMIN')")
    @Operation(summary = "Find dispatch by ID")
    public ResponseEntity<DispatchResponse> findById(@PathVariable("id") Integer id) {
        var res = queryDispatchUseCase.findById(id);
        return ResponseEntity.ok(mapper.toResponse(res));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PRESIDENT', 'BRANCH_ADMIN')")
    @Operation(summary = "Search dispatches with pagination")
    public ResponseEntity<PageResponse<DispatchResponse>> findAll(
            @RequestParam(name = "warehouseId", required = false) Integer warehouseId,
            @RequestParam(name = "orderId", required = false) Integer orderId,
            @RequestParam(name = "status", required = false) DispatchStatus status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(PageResponse.from(
                queryDispatchUseCase.findAll(warehouseId, orderId, status, PageQuery.of(page, size)),
                mapper::toResponse));
    }
}

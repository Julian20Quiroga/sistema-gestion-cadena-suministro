package com.logisync.sistema_cadena_suministro.infrastructure.controllers;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryInventoryUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryMovementUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.RegisterAdjustmentUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.RegisterEntryUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.RegisterExitUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.RegisterTransferUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.UndoMovementUseCase;
import com.logisync.sistema_cadena_suministro.application.queries.MovementFilter;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.RegisterAdjustmentRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.RegisterEntryRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.RegisterExitRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.RegisterTransferRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.InventoryLevelResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.MovementResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.PageResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.ProductAvailabilityResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.ProductRotationResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers.InventoryRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryRestMapper restMapper;
    private final RegisterEntryUseCase registerEntryUseCase;
    private final RegisterExitUseCase registerExitUseCase;
    private final RegisterAdjustmentUseCase registerAdjustmentUseCase;
    private final RegisterTransferUseCase registerTransferUseCase;
    private final UndoMovementUseCase undoMovementUseCase;
    private final QueryInventoryUseCase queryInventoryUseCase;
    private final QueryMovementUseCase queryMovementUseCase;

    @PreAuthorize("hasAnyRole('PRESIDENT','BRANCH_ADMIN')")
    @PostMapping("/entries")
    public ResponseEntity<MovementResponse> entry(@Valid @RequestBody RegisterEntryRequest request) {
        return created(restMapper.toResponse(registerEntryUseCase.registerEntry(restMapper.toCommand(request))));
    }

    @PreAuthorize("hasAnyRole('PRESIDENT','BRANCH_ADMIN')")
    @PostMapping("/exits")
    public ResponseEntity<MovementResponse> exit(@Valid @RequestBody RegisterExitRequest request) {
        return created(restMapper.toResponse(registerExitUseCase.registerExit(restMapper.toCommand(request))));
    }

    @PreAuthorize("hasRole('PRESIDENT')")
    @PostMapping("/adjustments")
    public ResponseEntity<MovementResponse> adjustment(@Valid @RequestBody RegisterAdjustmentRequest request) {
        return created(restMapper.toResponse(
                registerAdjustmentUseCase.registerAdjustment(restMapper.toCommand(request))));
    }

    @PreAuthorize("hasAnyRole('PRESIDENT','BRANCH_ADMIN')")
    @PostMapping("/transfers")
    public ResponseEntity<MovementResponse> transfer(@Valid @RequestBody RegisterTransferRequest request) {
        return created(restMapper.toResponse(
                registerTransferUseCase.registerTransfer(restMapper.toCommand(request))));
    }

    @PreAuthorize("hasRole('PRESIDENT')")
    @PostMapping("/movements/{id}/undo")
    public ResponseEntity<MovementResponse> undo(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(restMapper.toResponse(undoMovementUseCase.undo(id)));
    }

    @PreAuthorize("hasAnyRole('PRESIDENT','BRANCH_ADMIN')")
    @GetMapping("/levels")
    public ResponseEntity<PageResponse<InventoryLevelResponse>> levels(
            @RequestParam(required = false) Integer productId,
            @RequestParam(required = false) Integer warehouseId,
            @RequestParam(required = false) Integer storageSpaceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(PageResponse.from(
                queryInventoryUseCase.findLevels(productId, warehouseId, storageSpaceId, PageQuery.of(page, size)),
                restMapper::toResponse));
    }

    @PreAuthorize("hasAnyRole('PRESIDENT','BRANCH_ADMIN')")
    @GetMapping("/products/{productId}/availability")
    public ResponseEntity<ProductAvailabilityResponse> availability(@PathVariable("productId") Integer productId) {
        return ResponseEntity.ok(restMapper.toResponse(queryInventoryUseCase.availability(productId)));
    }

    @PreAuthorize("hasAnyRole('PRESIDENT','BRANCH_ADMIN')")
    @GetMapping("/products/{productId}/rotation")
    public ResponseEntity<ProductRotationResponse> rotation(@PathVariable("productId") Integer productId,
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(restMapper.toResponse(queryInventoryUseCase.rotation(productId, days)));
    }

    @PreAuthorize("hasAnyRole('PRESIDENT','BRANCH_ADMIN')")
    @GetMapping("/movements")
    public ResponseEntity<PageResponse<MovementResponse>> movements(
            @RequestParam(required = false) Integer productId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(PageResponse.from(
                queryMovementUseCase.findMovements(new MovementFilter(productId, type, from, to),
                        PageQuery.of(page, size)),
                restMapper::toResponse));
    }

    private ResponseEntity<MovementResponse> created(MovementResponse response) {
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

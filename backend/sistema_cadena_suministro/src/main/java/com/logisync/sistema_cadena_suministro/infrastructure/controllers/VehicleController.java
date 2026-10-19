package com.logisync.sistema_cadena_suministro.infrastructure.controllers;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreateVehicleUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.OptimizeRouteUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryVehicleUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.RotateVehicleApiKeyUseCase;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreateVehicleRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.OptimizeRouteRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.OptimizedRouteResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.PageResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.VehicleResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers.VehicleRestMapper;
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
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicles & Route Optimization", description = "Vehicle fleet management and routing optimization algorithms")
@RequiredArgsConstructor
public class VehicleController {

    private final CreateVehicleUseCase createVehicleUseCase;
    private final RotateVehicleApiKeyUseCase rotateVehicleApiKeyUseCase;
    private final QueryVehicleUseCase queryVehicleUseCase;
    private final OptimizeRouteUseCase optimizeRouteUseCase;
    private final VehicleRestMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('PRESIDENT')")
    @Operation(summary = "Register vehicle and issue IoT device API key")
    public ResponseEntity<VehicleResponse> create(@RequestBody @Valid CreateVehicleRequest request) {
        var res = createVehicleUseCase.create(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(res));
    }

    @PostMapping("/{id}/rotate-api-key")
    @PreAuthorize("hasRole('PRESIDENT')")
    @Operation(summary = "Rotate IoT vehicle API key")
    public ResponseEntity<VehicleResponse> rotateApiKey(@PathVariable("id") Integer id) {
        var res = rotateVehicleApiKeyUseCase.rotateApiKey(id);
        return ResponseEntity.ok(mapper.toResponse(res));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRESIDENT', 'BRANCH_ADMIN')")
    @Operation(summary = "Find vehicle by ID")
    public ResponseEntity<VehicleResponse> findById(@PathVariable("id") Integer id) {
        var res = queryVehicleUseCase.findById(id);
        return ResponseEntity.ok(mapper.toResponse(res));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PRESIDENT', 'BRANCH_ADMIN')")
    @Operation(summary = "Search vehicles with pagination")
    public ResponseEntity<PageResponse<VehicleResponse>> findAll(
            @RequestParam(name = "plate", required = false) String plate,
            @RequestParam(name = "status", required = false) SoftDeleteStatus status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(PageResponse.from(
                queryVehicleUseCase.findAll(plate, status, PageQuery.of(page, size)),
                mapper::toResponse));
    }

    @PostMapping("/optimize-route")
    @PreAuthorize("hasAnyRole('PRESIDENT', 'BRANCH_ADMIN')")
    @Operation(summary = "Calculate optimal delivery route for order stops")
    public ResponseEntity<OptimizedRouteResponse> optimizeRoute(@RequestBody @Valid OptimizeRouteRequest request) {
        var res = optimizeRouteUseCase.optimizeRoute(mapper.toRouteCommand(request));
        return ResponseEntity.ok(mapper.toRouteResponse(res));
    }
}

package com.logisync.sistema_cadena_suministro.infrastructure.controllers;

import com.logisync.sistema_cadena_suministro.application.commands.CreateLayoutSnapshotCommand;
import com.logisync.sistema_cadena_suministro.application.ports.driven.ManageLayoutSnapshotUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.OptimizeWarehouseLayoutUseCase;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreateLayoutSnapshotRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.LayoutOptimizationResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.LayoutSnapshotResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers.StorageOptimizationRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses/{id}")
@RequiredArgsConstructor
public class WarehouseOptimizationController {

    private final OptimizeWarehouseLayoutUseCase optimizeWarehouseLayoutUseCase;
    private final ManageLayoutSnapshotUseCase manageLayoutSnapshotUseCase;
    private final StorageOptimizationRestMapper mapper;

    /**
     * Ejecuta el análisis de reubicación y optimización de espacios de almacenamiento en la bodega.
     * Patrones aplicados: Visitor (RelocationProposalVisitor recorre espacios) y Memento (crea snapshot de respaldo previo).
     */
    @PostMapping("/optimize-layout")
    public ResponseEntity<LayoutOptimizationResponse> optimizeLayout(
            @PathVariable("id") Integer warehouseId,
            @RequestParam(value = "autoSnapshot", defaultValue = "true") boolean autoSnapshot) {
        var appResp = optimizeWarehouseLayoutUseCase.optimize(warehouseId, autoSnapshot);
        return ResponseEntity.ok(mapper.toResponse(appResp));
    }

    /**
     * Crea un punto de restauración o captura de la distribución física actual de la bodega.
     * Patrón aplicado: Memento (WarehouseLayoutOriginator genera un StorageLayoutMemento serializado).
     */
    @PostMapping("/snapshots")
    public ResponseEntity<LayoutSnapshotResponse> createSnapshot(
            @PathVariable("id") Integer warehouseId,
            @Valid @RequestBody CreateLayoutSnapshotRequest request) {
        var appResp = manageLayoutSnapshotUseCase.createSnapshot(
                new CreateLayoutSnapshotCommand(warehouseId, request.snapshotName())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(appResp));
    }

    /**
     * Lista todos los snapshots o puntos de restauración históricos registrados para la bodega.
     */
    @GetMapping("/snapshots")
    public ResponseEntity<List<LayoutSnapshotResponse>> listSnapshots(@PathVariable("id") Integer warehouseId) {
        var list = manageLayoutSnapshotUseCase.listSnapshots(warehouseId).stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    /**
     * Restaura la configuración jerárquica y capacidades de la bodega a un estado previo guardado.
     * Patrón aplicado: Memento (restaura el layout estructural a partir del snapshot seleccionado).
     */
    @PostMapping("/snapshots/{snapshotId}/restore")
    public ResponseEntity<LayoutSnapshotResponse> restoreSnapshot(
            @PathVariable("id") Integer warehouseId,
            @PathVariable("snapshotId") Integer snapshotId) {
        var appResp = manageLayoutSnapshotUseCase.restoreSnapshot(warehouseId, snapshotId);
        return ResponseEntity.ok(mapper.toResponse(appResp));
    }
}

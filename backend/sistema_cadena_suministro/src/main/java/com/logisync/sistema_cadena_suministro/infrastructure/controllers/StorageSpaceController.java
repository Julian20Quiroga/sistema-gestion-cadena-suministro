package com.logisync.sistema_cadena_suministro.infrastructure.controllers;

import com.logisync.sistema_cadena_suministro.application.ports.driven.CreateStorageSpaceUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.DeleteStorageSpaceUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryStorageStructureUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.UpdateStorageSpaceUseCase;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreateStorageSpaceRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.UpdateStorageSpaceRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.FreeLocationResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.StorageNodeResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.StorageSpaceResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers.StorageRestMapper;
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

import java.util.List;

@RestController
@RequestMapping("/api/warehouses/{warehouseId}")
@RequiredArgsConstructor
public class StorageSpaceController {

    private final StorageRestMapper restMapper;
    private final CreateStorageSpaceUseCase createStorageSpaceUseCase;
    private final UpdateStorageSpaceUseCase updateStorageSpaceUseCase;
    private final DeleteStorageSpaceUseCase deleteStorageSpaceUseCase;
    private final QueryStorageStructureUseCase queryStorageStructureUseCase;

    @PreAuthorize("hasRole('PRESIDENT')")
    @PostMapping("/spaces")
    public ResponseEntity<StorageSpaceResponse> create(@PathVariable("warehouseId") Integer warehouseId,
            @Valid @RequestBody CreateStorageSpaceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restMapper.toResponse(
                createStorageSpaceUseCase.create(warehouseId, restMapper.toCommandCreate(request))));
    }

    @PreAuthorize("hasRole('PRESIDENT')")
    @PutMapping("/spaces/{spaceId}")
    public ResponseEntity<StorageSpaceResponse> update(@PathVariable("warehouseId") Integer warehouseId,
            @PathVariable("spaceId") Integer spaceId, @Valid @RequestBody UpdateStorageSpaceRequest request) {
        return ResponseEntity.ok(restMapper.toResponse(
                updateStorageSpaceUseCase.update(warehouseId, spaceId, restMapper.toCommandUpdate(request))));
    }

    @PreAuthorize("hasRole('PRESIDENT')")
    @DeleteMapping("/spaces/{spaceId}")
    public ResponseEntity<Void> delete(@PathVariable("warehouseId") Integer warehouseId,
            @PathVariable("spaceId") Integer spaceId) {
        deleteStorageSpaceUseCase.delete(warehouseId, spaceId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('PRESIDENT','BRANCH_ADMIN')")
    @GetMapping("/structure")
    public ResponseEntity<StorageNodeResponse> structure(@PathVariable("warehouseId") Integer warehouseId) {
        return ResponseEntity.ok(restMapper.toResponse(queryStorageStructureUseCase.structure(warehouseId)));
    }

    @PreAuthorize("hasAnyRole('PRESIDENT','BRANCH_ADMIN')")
    @GetMapping("/free-locations")
    public ResponseEntity<List<FreeLocationResponse>> freeLocations(@PathVariable("warehouseId") Integer warehouseId,
            @RequestParam(defaultValue = "1") long minFree) {
        return ResponseEntity.ok(queryStorageStructureUseCase.freeLocations(warehouseId, minFree).stream()
                .map(restMapper::toResponse).toList());
    }
}

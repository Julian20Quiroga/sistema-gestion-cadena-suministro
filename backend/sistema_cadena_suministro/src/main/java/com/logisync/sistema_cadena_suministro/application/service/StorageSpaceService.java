package com.logisync.sistema_cadena_suministro.application.service;

import com.logisync.sistema_cadena_suministro.application.commands.CreateStorageSpaceCommand;
import com.logisync.sistema_cadena_suministro.application.commands.UpdateStorageSpaceCommand;
import com.logisync.sistema_cadena_suministro.application.mappers.StorageApplicationMapper;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreateStorageSpaceUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.DeleteStorageSpaceUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryStorageStructureUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.UpdateStorageSpaceUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.StorageSpaceRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.StoredItemsReader;
import com.logisync.sistema_cadena_suministro.application.ports.driving.WarehouseRepository;
import com.logisync.sistema_cadena_suministro.application.responses.FreeLocationAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.StorageNodeAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.StorageSpaceAppResponse;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.models.StorageSpace;
import com.logisync.sistema_cadena_suministro.domain.models.Warehouse;
import com.logisync.sistema_cadena_suministro.domain.storage.FreeLocationVisitor;
import com.logisync.sistema_cadena_suministro.domain.storage.StorageTreeAssembler;
import com.logisync.sistema_cadena_suministro.domain.storage.StructureViewVisitor;
import com.logisync.sistema_cadena_suministro.domain.storage.WarehouseNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageSpaceService implements CreateStorageSpaceUseCase, UpdateStorageSpaceUseCase,
        DeleteStorageSpaceUseCase, QueryStorageStructureUseCase {

    private final StorageApplicationMapper mapper;
    private final WarehouseRepository warehouseRepository;
    private final StorageSpaceRepository storageSpaceRepository;
    private final StoredItemsReader storedItemsReader;

    @Override
    @Transactional
    public StorageSpaceAppResponse create(Integer warehouseId, CreateStorageSpaceCommand command) {
        requireActiveWarehouse(warehouseId);
        StorageSpace parent = command.parentId() == null ? null : requireSpace(warehouseId, command.parentId());
        StorageSpace space = StorageSpace.create(command.name(), warehouseId, command.type(), command.capacity(),
                parent);
        return mapper.toResponse(storageSpaceRepository.create(space));
    }

    @Override
    @Transactional
    public StorageSpaceAppResponse update(Integer warehouseId, Integer spaceId, UpdateStorageSpaceCommand command) {
        StorageSpace space = requireSpace(warehouseId, spaceId);
        space.rename(command.name(), command.capacity(), storedItemsReader.unitsInSpace(spaceId));
        return mapper.toResponse(storageSpaceRepository.update(space));
    }

    @Override
    @Transactional
    public void delete(Integer warehouseId, Integer spaceId) {
        requireSpace(warehouseId, spaceId);
        if (storageSpaceRepository.hasActiveChildren(spaceId)) {
            throw new BusinessRuleException("Space " + spaceId + " still contains active child spaces");
        }
        if (storedItemsReader.unitsInSpace(spaceId) > 0) {
            throw new BusinessRuleException("Space " + spaceId + " still holds stock");
        }
        storageSpaceRepository.delete(spaceId);
    }

    @Override
    @Transactional(readOnly = true)
    public StorageNodeAppResponse structure(Integer warehouseId) {
        return mapper.toResponse(loadTree(warehouseId).accept(new StructureViewVisitor()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FreeLocationAppResponse> freeLocations(Integer warehouseId, long minFreeUnits) {
        return loadTree(warehouseId).accept(new FreeLocationVisitor(minFreeUnits)).stream()
                .map(mapper::toResponse).toList();
    }

    private WarehouseNode loadTree(Integer warehouseId) {
        Warehouse warehouse = requireActiveWarehouse(warehouseId);
        return StorageTreeAssembler.assemble(warehouse, storageSpaceRepository.findActiveByWarehouse(warehouseId),
                storedItemsReader.itemsBySpace(warehouseId));
    }

    private Warehouse requireActiveWarehouse(Integer warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", warehouseId));
        if (!warehouse.isActive()) {
            throw new BusinessRuleException("Warehouse " + warehouseId + " is not active");
        }
        return warehouse;
    }

    private StorageSpace requireSpace(Integer warehouseId, Integer spaceId) {
        return storageSpaceRepository.findById(spaceId)
                .filter(s -> s.isActive() && s.getWarehouseId().equals(warehouseId))
                .orElseThrow(() -> new ResourceNotFoundException("Storage space", spaceId));
    }
}

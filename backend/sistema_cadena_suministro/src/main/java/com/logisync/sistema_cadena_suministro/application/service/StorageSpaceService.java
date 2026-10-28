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

/**
 * Servicio de aplicación para la gestión de espacios jerárquicos de almacenamiento en almacenes.
 * Implementa los patrones:
 * - Composite: Estructuración multinivel uniforme (Zonas, Pasillos, Racks, Bahías).
 * - Visitor: Recorridos de auditoría estructural y localización de bahías libres.
 */
@Service
@RequiredArgsConstructor
public class StorageSpaceService implements CreateStorageSpaceUseCase, UpdateStorageSpaceUseCase,
        DeleteStorageSpaceUseCase, QueryStorageStructureUseCase {

    private final StorageApplicationMapper mapper;
    private final WarehouseRepository warehouseRepository;
    private final StorageSpaceRepository storageSpaceRepository;
    private final StoredItemsReader storedItemsReader;

    /**
     * Registra un nuevo espacio físico (Zona, Rack o Bahía) anidándolo a su espacio contenedor si aplica.
     * Patrón aplicado: Composite (creación de nodos hoja o compuestos en la jerarquía).
     */
    @Override
    @Transactional
    public StorageSpaceAppResponse create(Integer warehouseId, CreateStorageSpaceCommand command) {
        requireActiveWarehouse(warehouseId);
        StorageSpace parent = command.parentId() == null ? null : requireSpace(warehouseId, command.parentId());
        StorageSpace space = StorageSpace.create(command.name(), warehouseId, command.type(), command.capacity(),
                parent);
        return mapper.toResponse(storageSpaceRepository.create(space));
    }

    /**
     * Modifica el nombre o capacidad volumétrica de un espacio de almacenamiento.
     */
    @Override
    @Transactional
    public StorageSpaceAppResponse update(Integer warehouseId, Integer spaceId, UpdateStorageSpaceCommand command) {
        StorageSpace space = requireSpace(warehouseId, spaceId);
        space.rename(command.name(), command.capacity(), storedItemsReader.unitsInSpace(spaceId));
        return mapper.toResponse(storageSpaceRepository.update(space));
    }

    /**
     * Elimina un espacio de almacenamiento comprobando que no contenga sub-espacios activos ni stock retenido.
     */
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

    /**
     * Obtiene la vista estructurada en árbol del almacén calculando capacidades totales recursivamente.
     * Patrones aplicados: Composite (estructura en árbol) y Visitor (StructureViewVisitor).
     */
    @Override
    @Transactional(readOnly = true)
    public StorageNodeAppResponse structure(Integer warehouseId) {
        return mapper.toResponse(loadTree(warehouseId).accept(new StructureViewVisitor()));
    }

    /**
     * Localiza bahías de almacenamiento con capacidad disponible igual o superior a las unidades solicitadas.
     * Patrón aplicado: Visitor (FreeLocationVisitor busca y filtra ubicaciones libres en el árbol).
     */
    @Override
    @Transactional(readOnly = true)
    public List<FreeLocationAppResponse> freeLocations(Integer warehouseId, long minFreeUnits) {
        return loadTree(warehouseId).accept(new FreeLocationVisitor(minFreeUnits)).stream()
                .map(mapper::toResponse).toList();
    }

    /**
     * Carga y ensambla el árbol Composite del almacén junto con los balances de inventario actuales.
     */
    private WarehouseNode loadTree(Integer warehouseId) {
        Warehouse warehouse = requireActiveWarehouse(warehouseId);
        return StorageTreeAssembler.assemble(warehouse, storageSpaceRepository.findActiveByWarehouse(warehouseId),
                storedItemsReader.itemsBySpace(warehouseId));
    }

    /**
     * Valida que el almacén exista y se encuentre en estado activo.
     */
    private Warehouse requireActiveWarehouse(Integer warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", warehouseId));
        if (!warehouse.isActive()) {
            throw new BusinessRuleException("Warehouse " + warehouseId + " is not active");
        }
        return warehouse;
    }

    /**
     * Valida que el espacio físico exista y pertenezca al almacén indicado.
     */
    private StorageSpace requireSpace(Integer warehouseId, Integer spaceId) {
        return storageSpaceRepository.findById(spaceId)
                .filter(s -> s.isActive() && s.getWarehouseId().equals(warehouseId))
                .orElseThrow(() -> new ResourceNotFoundException("Storage space", spaceId));
    }
}

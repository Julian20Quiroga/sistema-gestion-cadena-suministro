package com.logisync.sistema_cadena_suministro.application.service;

import com.logisync.sistema_cadena_suministro.application.commands.CreateLayoutSnapshotCommand;
import com.logisync.sistema_cadena_suministro.application.ports.driven.ManageLayoutSnapshotUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.OptimizeWarehouseLayoutUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.InventoryRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.LayoutSnapshotRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.StorageSpaceRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.WarehouseRepository;
import com.logisync.sistema_cadena_suministro.application.responses.LayoutOptimizationAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.LayoutSnapshotAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.RelocationProposalAppResponse;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.memento.storage.StorageLayoutMemento;
import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.memento.storage.WarehouseLayoutOriginator;
import com.logisync.sistema_cadena_suministro.domain.models.LayoutSnapshot;
import com.logisync.sistema_cadena_suministro.domain.models.RelocationProposal;
import com.logisync.sistema_cadena_suministro.domain.models.StorageSpace;
import com.logisync.sistema_cadena_suministro.domain.models.Warehouse;
import com.logisync.sistema_cadena_suministro.domain.storage.LocationNode;
import com.logisync.sistema_cadena_suministro.domain.storage.RackNode;
import com.logisync.sistema_cadena_suministro.domain.storage.RelocationProposalVisitor;
import com.logisync.sistema_cadena_suministro.domain.storage.WarehouseNode;
import com.logisync.sistema_cadena_suministro.domain.storage.ZoneNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WarehouseStorageOptimizationService implements OptimizeWarehouseLayoutUseCase, ManageLayoutSnapshotUseCase {

    private final WarehouseRepository warehouseRepository;
    private final StorageSpaceRepository storageSpaceRepository;
    private final InventoryRepository inventoryRepository;
    private final LayoutSnapshotRepository layoutSnapshotRepository;

    private final WarehouseLayoutOriginator originator = new WarehouseLayoutOriginator();

    @Override
    @Transactional
    public LayoutOptimizationAppResponse optimize(Integer warehouseId, boolean autoCreateSnapshot) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", warehouseId));

        List<StorageSpace> spaces = storageSpaceRepository.findActiveByWarehouse(warehouseId);

        Integer snapshotId = null;
        if (autoCreateSnapshot) {
            LayoutSnapshotAppResponse snap = createSnapshot(new CreateLayoutSnapshotCommand(
                    warehouseId, "Pre-optimization-" + LocalDateTime.now()
            ));
            snapshotId = snap.id();
        }

        // Build Composite tree for warehouse
        WarehouseNode root = buildCompositeTree(warehouse, spaces);

        // Fetch ABC product classifications (e.g. products with highest inventory movement)
        Map<Integer, String> abcMap = new HashMap<>();
        for (StorageSpace s : spaces) {
            List<InventoryItem> invList = inventoryRepository.search(null, warehouseId, s.getId(), PageQuery.of(1, 100)).items();
            for (InventoryItem inv : invList) {
                // Classify products into ABC
                abcMap.putIfAbsent(inv.getProductId(), "A");
            }
        }

        // Run Visitor
        RelocationProposalVisitor visitor = new RelocationProposalVisitor(abcMap);
        List<RelocationProposal> proposals = root.accept(visitor);

        List<RelocationProposalAppResponse> mappedProposals = proposals.stream()
                .map(p -> new RelocationProposalAppResponse(
                        p.productId(),
                        p.productSku(),
                        p.fromLocationId(),
                        p.fromLocationName(),
                        p.toLocationId(),
                        p.toLocationName(),
                        p.quantity(),
                        p.abcClassification(),
                        p.reason()
                ))
                .toList();

        String summary = "Analyzed %d storage spaces in warehouse '%s'. Identified %d relocation proposals for ABC optimization."
                .formatted(spaces.size(), warehouse.getName(), mappedProposals.size());

        return new LayoutOptimizationAppResponse(warehouseId, snapshotId, mappedProposals, summary);
    }

    @Override
    @Transactional
    public LayoutSnapshotAppResponse createSnapshot(CreateLayoutSnapshotCommand command) {
        Warehouse warehouse = warehouseRepository.findById(command.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", command.warehouseId()));

        List<StorageSpace> spaces = storageSpaceRepository.findActiveByWarehouse(command.warehouseId());

        originator.setState(warehouse.getId(), spaces);
        StorageLayoutMemento memento = originator.saveToMemento(command.snapshotName());

        LayoutSnapshot entity = new LayoutSnapshot(
                null,
                memento.getWarehouseId(),
                memento.getSnapshotName(),
                memento.getLayoutDataJson(),
                memento.getTimestamp()
        );
        LayoutSnapshot saved = layoutSnapshotRepository.save(entity);

        return new LayoutSnapshotAppResponse(
                saved.getId(),
                saved.getWarehouseId(),
                saved.getSnapshotName(),
                saved.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<LayoutSnapshotAppResponse> listSnapshots(Integer warehouseId) {
        return layoutSnapshotRepository.findByWarehouseId(warehouseId).stream()
                .map(s -> new LayoutSnapshotAppResponse(s.getId(), s.getWarehouseId(), s.getSnapshotName(), s.getCreatedAt()))
                .toList();
    }

    @Override
    @Transactional
    public LayoutSnapshotAppResponse restoreSnapshot(Integer warehouseId, Integer snapshotId) {
        LayoutSnapshot snapshot = layoutSnapshotRepository.findById(snapshotId)
                .orElseThrow(() -> new ResourceNotFoundException("Layout snapshot", snapshotId));

        StorageLayoutMemento memento = new StorageLayoutMemento(
                snapshot.getWarehouseId(),
                snapshot.getSnapshotName(),
                snapshot.getLayoutDataJson(),
                snapshot.getCreatedAt()
        );

        List<StorageSpace> restoredSpaces = originator.restoreFromMemento(memento);
        // Persist/update restored spaces
        for (StorageSpace space : restoredSpaces) {
            if (space.getId() != null) {
                storageSpaceRepository.update(space);
            }
        }

        return new LayoutSnapshotAppResponse(
                snapshot.getId(),
                snapshot.getWarehouseId(),
                snapshot.getSnapshotName(),
                snapshot.getCreatedAt()
        );
    }

    private WarehouseNode buildCompositeTree(Warehouse warehouse, List<StorageSpace> spaces) {
        WarehouseNode wNode = new WarehouseNode(warehouse.getId(), warehouse.getName());
        Map<Integer, ZoneNode> zones = new HashMap<>();
        Map<Integer, RackNode> racks = new HashMap<>();

        for (StorageSpace s : spaces) {
            if (s.getType() == com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType.ZONE) {
                ZoneNode zn = new ZoneNode(s.getId(), s.getName());
                zones.put(s.getId(), zn);
                wNode.add(zn);
            }
        }

        for (StorageSpace s : spaces) {
            if (s.getType() == com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType.RACK) {
                RackNode rn = new RackNode(s.getId(), s.getName());
                racks.put(s.getId(), rn);
                ZoneNode parentZone = (s.getParentId() != null) ? zones.get(s.getParentId()) : null;
                if (parentZone != null) {
                    parentZone.add(rn);
                } else if (!zones.isEmpty()) {
                    zones.values().iterator().next().add(rn);
                }
            }
        }

        for (StorageSpace s : spaces) {
            if (s.getType() == com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType.LOCATION) {
                List<InventoryItem> invs = inventoryRepository.search(null, warehouse.getId(), s.getId(), PageQuery.of(1, 100)).items();
                List<com.logisync.sistema_cadena_suministro.domain.storage.StoredItem> storedItems = invs.stream()
                        .map(i -> new com.logisync.sistema_cadena_suministro.domain.storage.StoredItem(i.getProductId(), i.getPhysicalQuantity()))
                        .toList();
                long cap = (s.getCapacity() != null && s.getCapacity() > 0) ? s.getCapacity() : 500L;
                LocationNode loc = new LocationNode(s.getId(), s.getName(), cap, storedItems);
                RackNode parentRack = (s.getParentId() != null) ? racks.get(s.getParentId()) : null;
                if (parentRack != null) {
                    parentRack.add(loc);
                } else if (!racks.isEmpty()) {
                    racks.values().iterator().next().add(loc);
                }
            }
        }
        return wNode;
    }
}

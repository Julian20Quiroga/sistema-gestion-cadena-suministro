package com.logisync.sistema_cadena_suministro.application.service;

import com.logisync.sistema_cadena_suministro.application.commands.CreateLayoutSnapshotCommand;
import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.ports.driven.ManageLayoutSnapshotUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.OptimizeWarehouseLayoutUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.InventoryRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.OrderRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.ProductRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.LayoutSnapshotRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.StorageSpaceRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.WarehouseRepository;
import com.logisync.sistema_cadena_suministro.application.responses.LayoutOptimizationAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.LayoutSnapshotAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.RelocationProposalAppResponse;
import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.memento.storage.StorageLayoutMemento;
import com.logisync.sistema_cadena_suministro.domain.memento.storage.WarehouseLayoutOriginator;
import com.logisync.sistema_cadena_suministro.domain.models.LayoutSnapshot;
import com.logisync.sistema_cadena_suministro.domain.models.RelocationProposal;
import com.logisync.sistema_cadena_suministro.domain.models.Product;
import com.logisync.sistema_cadena_suministro.domain.models.StorageSpace;
import com.logisync.sistema_cadena_suministro.domain.models.Warehouse;
import com.logisync.sistema_cadena_suministro.domain.storage.LocationNode;
import com.logisync.sistema_cadena_suministro.domain.storage.RackNode;
import com.logisync.sistema_cadena_suministro.domain.storage.RelocationProposalVisitor;
import com.logisync.sistema_cadena_suministro.domain.storage.StoredItem;
import com.logisync.sistema_cadena_suministro.domain.storage.WarehouseNode;
import com.logisync.sistema_cadena_suministro.domain.storage.ZoneNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Servicio de aplicación para la optimización de distribución física de almacenes (Layout ABC).
 * Implementa los patrones:
 * - Visitor: Realiza el análisis de rotación y propuestas de reubicación sin alterar las entidades.
 * - Composite: Modela la estructura jerárquica de nodos del almacén (Zonas, Racks, Bahías).
 * - Memento: Captura instantáneas previas y permite la reversión o rollback histórico.
 */
@Service
@RequiredArgsConstructor
public class WarehouseStorageOptimizationService implements OptimizeWarehouseLayoutUseCase, ManageLayoutSnapshotUseCase {

    private final WarehouseRepository warehouseRepository;
    private final StorageSpaceRepository storageSpaceRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final LayoutSnapshotRepository layoutSnapshotRepository;

    private final WarehouseLayoutOriginator originator = new WarehouseLayoutOriginator();

    /**
     * Ejecuta el análisis de optimización de distribución física (Layout ABC) de un almacén.
     * Patrones aplicados:
     * - Composite: Construye el árbol jerárquico de zonas, racks y bahías.
     * - Visitor: Recorre el árbol con RelocationProposalVisitor para detectar artículos de alta rotación (Clase A) mal ubicados.
     * - Memento: Si autoCreateSnapshot es true, genera un snapshot previo de seguridad.
     */
    @Override
    @Transactional
    public LayoutOptimizationAppResponse optimize(Integer warehouseId, boolean autoCreateSnapshot) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", warehouseId));

        List<StorageSpace> spaces = storageSpaceRepository.findActiveByWarehouse(warehouseId).stream()
                .sorted(Comparator.comparing(StorageSpace::getId))
                .toList();

        Integer snapshotId = null;
        if (autoCreateSnapshot) {
            LayoutSnapshotAppResponse snap = createSnapshot(new CreateLayoutSnapshotCommand(
                    warehouseId, "Pre-optimization-" + LocalDateTime.now()
            ));
            snapshotId = snap.id();
        }

        // Construir el árbol Composite del almacén
        WarehouseNode root = buildCompositeTree(warehouse, spaces);

        // Clasificar productos para el análisis ABC
        Map<Integer, Long> demandByProduct = new HashMap<>();
        LocalDateTime demandWindowStart = LocalDateTime.now(ZoneId.of("America/Bogota")).minusDays(180);
        orderRepository.findDeliveredDemand(demandWindowStart, LocalDateTime.now(ZoneId.of("America/Bogota")), null)
                .forEach(record -> demandByProduct.merge(record.productId(), record.quantity().longValue(), Long::sum));

        Map<Integer, String> abcMap = classifyDemand(demandByProduct);
        for (StorageSpace s : spaces) {
            List<InventoryItem> invList = inventoryRepository.search(null, warehouseId, s.getId(), PageQuery.of(0, PageQuery.MAX_SIZE)).items();
            for (InventoryItem inv : invList) {
                abcMap.putIfAbsent(inv.getProductId(), "C");
            }
        }

        // Ejecutar el patrón Visitor sobre la estructura compuesta
        RelocationProposalVisitor visitor = new RelocationProposalVisitor(abcMap);
        List<RelocationProposal> proposals = root.accept(visitor);

        List<RelocationProposalAppResponse> mappedProposals = proposals.stream()
                .map(p -> {
                    String sku = productRepository.findById(p.productId()).map(Product::getSku).orElse(p.productSku());
                    return new RelocationProposalAppResponse(
                            p.productId(),
                            sku,
                            p.fromLocationId(),
                            p.fromLocationName() + " (#" + p.fromLocationId() + ")",
                            p.toLocationId(),
                            p.toLocationName() + " (#" + p.toLocationId() + ")",
                            p.quantity(),
                            p.abcClassification(),
                            p.reason()
                    );
                })
                .toList();

        String summary = "Se analizaron %d espacios activos de la bodega '%s' y se encontraron %d propuestas de reubicación ABC."
                .formatted(spaces.size(), warehouse.getName(), mappedProposals.size());

        return new LayoutOptimizationAppResponse(warehouseId, snapshotId, mappedProposals, summary);
    }

    /** Classifica por volumen de unidades en pedidos entregados: A hasta 80%, B hasta 95%, resto C. */
    private Map<Integer, String> classifyDemand(Map<Integer, Long> demandByProduct) {
        Map<Integer, String> classes = new HashMap<>();
        long totalDemand = demandByProduct.values().stream().mapToLong(Long::longValue).sum();
        if (totalDemand <= 0) return classes;

        List<Map.Entry<Integer, Long>> ranked = demandByProduct.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .toList();
        long cumulative = 0;
        for (Map.Entry<Integer, Long> entry : ranked) {
            double priorShare = (double) cumulative / totalDemand;
            classes.put(entry.getKey(), priorShare < 0.80 ? "A" : priorShare < 0.95 ? "B" : "C");
            cumulative += entry.getValue();
        }
        return classes;
    }

    /**
     * Captura una instantánea del estado físico del almacén.
     * Patrón aplicado: Memento (guarda el estado serializado en un objeto Memento inmutable).
     */
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

    /**
     * Lista todos los snapshots históricos disponibles para un almacén.
     */
    @Override
    @Transactional(readOnly = true)
    public List<LayoutSnapshotAppResponse> listSnapshots(Integer warehouseId) {
        return layoutSnapshotRepository.findByWarehouseId(warehouseId).stream()
                .map(s -> new LayoutSnapshotAppResponse(s.getId(), s.getWarehouseId(), s.getSnapshotName(), s.getCreatedAt()))
                .toList();
    }

    /**
     * Restaura la configuración física de un almacén a partir de un snapshot previo (Rollback).
     * Patrón aplicado: Memento (recupera el estado persistido en el Memento y lo aplica en el originador).
     */
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

    /**
     * Construye la estructura en árbol Composite a partir de la lista plana de espacios de almacenamiento.
     * Patrón aplicado: Composite (organiza Zonas, Racks y Bahías en una estructura uniforme).
     */
    private WarehouseNode buildCompositeTree(Warehouse warehouse, List<StorageSpace> spaces) {
        WarehouseNode wNode = new WarehouseNode(warehouse.getId(), warehouse.getName());
        Map<Integer, ZoneNode> zones = new HashMap<>();
        Map<Integer, RackNode> racks = new HashMap<>();

        for (StorageSpace s : spaces) {
            if (s.getType() == StorageSpaceType.ZONE) {
                ZoneNode zn = new ZoneNode(s.getId(), s.getName());
                zones.put(s.getId(), zn);
                wNode.add(zn);
            }
        }

        for (StorageSpace s : spaces) {
            if (s.getType() == StorageSpaceType.RACK) {
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
            if (s.getType() == StorageSpaceType.LOCATION) {
                List<InventoryItem> invs = inventoryRepository.search(null, warehouse.getId(), s.getId(), PageQuery.of(0, PageQuery.MAX_SIZE)).items();
                List<StoredItem> storedItems = invs.stream()
                        .map(i -> new StoredItem(i.getProductId(), i.getPhysicalQuantity()))
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

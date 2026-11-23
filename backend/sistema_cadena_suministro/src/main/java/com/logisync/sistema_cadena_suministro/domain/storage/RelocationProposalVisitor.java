package com.logisync.sistema_cadena_suministro.domain.storage;

import com.logisync.sistema_cadena_suministro.domain.models.RelocationProposal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

/**
 * Visitante de la estructura física del almacén para el análisis de distribución ABC.
 * Patrón aplicado: Visitor (recorre el árbol Composite de almacenamiento evaluando
 * la eficiencia de ubicación de productos de alta rotación sin alterar las entidades del dominio).
 */
public class RelocationProposalVisitor implements StorageNodeVisitor<List<RelocationProposal>> {

    private final Map<Integer, String> productAbcClassification;
    private final List<LocationNode> allLocations = new ArrayList<>();

    public RelocationProposalVisitor(Map<Integer, String> productAbcClassification) {
        this.productAbcClassification = (productAbcClassification != null) ? productAbcClassification : Map.of();
    }

    /**
     * Visita el nodo raíz del almacén recolectando ubicaciones hijas y evaluando propuestas.
     */
    @Override
    public List<RelocationProposal> visitWarehouse(WarehouseNode warehouse) {
        collectLocations(warehouse);
        return evaluateRelocations();
    }

    /**
     * Visita un nodo de tipo Zona recolectando los racks y ubicaciones internas.
     */
    @Override
    public List<RelocationProposal> visitZone(ZoneNode zone) {
        collectLocations(zone);
        return evaluateRelocations();
    }

    /**
     * Visita un nodo de tipo Rack recolectando sus bahías físicas.
     */
    @Override
    public List<RelocationProposal> visitRack(RackNode rack) {
        collectLocations(rack);
        return evaluateRelocations();
    }

    /**
     * Visita una bahía o ubicación final registrándola para el análisis de proximidad y capacidad.
     */
    @Override
    public List<RelocationProposal> visitLocation(LocationNode location) {
        allLocations.add(location);
        return List.of();
    }

    /**
     * Recorre recursivamente los elementos hijos aceptando este visitante.
     */
    private void collectLocations(StorageContainer container) {
        for (StorageNode child : container.children()) {
            child.accept(this);
        }
    }

    /**
     * Algoritmo de análisis ABC: detecta artículos de alta rotación (Clase A) alojados en zonas profundas
     * y genera propuestas de reubicación hacia bahías frontales (Prime) con capacidad disponible.
     */
    private List<RelocationProposal> evaluateRelocations() {
        List<RelocationProposal> proposals = new ArrayList<>();
        if (allLocations.size() < 2) {
            return proposals;
        }

        int midpoint = allLocations.size() / 2;
        List<LocationNode> primeLocations = allLocations.subList(0, midpoint);
        List<LocationNode> deepLocations = allLocations.subList(midpoint, allLocations.size());
        Map<Integer, Long> remainingCapacity = new HashMap<>();
        primeLocations.forEach(location -> remainingCapacity.put(location.id(), location.freeCapacity()));

        for (LocationNode deep : deepLocations) {
            for (StoredItem item : deep.items()) {
                String abc = productAbcClassification.getOrDefault(item.productId(), "B");
                if ("A".equalsIgnoreCase(abc)) {
                    for (LocationNode prime : primeLocations) {
                        if (Objects.equals(prime.id(), deep.id())) continue;
                        long freeSpace = remainingCapacity.getOrDefault(prime.id(), 0L);
                        if (freeSpace >= item.quantity()) {
                            proposals.add(new RelocationProposal(
                                    item.productId(),
                                    "SKU-" + item.productId(),
                                    deep.id(),
                                    deep.name(),
                                    prime.id(),
                                    prime.name(),
                                    (int) item.quantity(),
                                    "A",
                                    "Producto clase A ubicado en una posición posterior; se sugiere moverlo a una ubicación anterior con capacidad disponible"
                            ));
                            remainingCapacity.put(prime.id(), freeSpace - item.quantity());
                            break;
                        }
                    }
                }
            }
        }
        return proposals;
    }
}

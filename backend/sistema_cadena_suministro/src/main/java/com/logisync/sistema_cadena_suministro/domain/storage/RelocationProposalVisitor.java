package com.logisync.sistema_cadena_suministro.domain.storage;

import com.logisync.sistema_cadena_suministro.domain.models.RelocationProposal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Visitor Pattern: Traverses warehouse composite tree to find suboptimal item placements
 * and generate ABC layout relocation proposals.
 */
public class RelocationProposalVisitor implements StorageNodeVisitor<List<RelocationProposal>> {

    private final Map<Integer, String> productAbcClassification; // ProductId -> "A", "B", "C"
    private final List<LocationNode> allLocations = new ArrayList<>();

    public RelocationProposalVisitor(Map<Integer, String> productAbcClassification) {
        this.productAbcClassification = (productAbcClassification != null) ? productAbcClassification : Map.of();
    }

    @Override
    public List<RelocationProposal> visitWarehouse(WarehouseNode warehouse) {
        collectLocations(warehouse);
        return evaluateRelocations();
    }

    @Override
    public List<RelocationProposal> visitZone(ZoneNode zone) {
        collectLocations(zone);
        return evaluateRelocations();
    }

    @Override
    public List<RelocationProposal> visitRack(RackNode rack) {
        collectLocations(rack);
        return evaluateRelocations();
    }

    @Override
    public List<RelocationProposal> visitLocation(LocationNode location) {
        allLocations.add(location);
        return List.of();
    }

    private void collectLocations(StorageContainer container) {
        for (StorageNode child : container.children()) {
            child.accept(this);
        }
    }

    private List<RelocationProposal> evaluateRelocations() {
        List<RelocationProposal> proposals = new ArrayList<>();
        if (allLocations.size() < 2) {
            return proposals;
        }

        // Divide locations into Prime (front/low index) and Deep (back/high index)
        int midpoint = allLocations.size() / 2;
        List<LocationNode> primeLocations = allLocations.subList(0, midpoint);
        List<LocationNode> deepLocations = allLocations.subList(midpoint, allLocations.size());

        // Check if high-rotation 'A' items are in deep locations while prime locations have capacity
        for (LocationNode deep : deepLocations) {
            for (StoredItem item : deep.items()) {
                String abc = productAbcClassification.getOrDefault(item.productId(), "B");
                if ("A".equalsIgnoreCase(abc)) {
                    // Try to find a prime location with free capacity
                    for (LocationNode prime : primeLocations) {
                        long freeSpace = prime.freeCapacity();
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
                                    "High-turnover Class A product placed in deep zone; moving to prime dispatch area"
                            ));
                            break;
                        }
                    }
                }
            }
        }
        return proposals;
    }
}

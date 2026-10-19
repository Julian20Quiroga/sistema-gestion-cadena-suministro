package com.logisync.sistema_cadena_suministro.domain.storage;

import com.logisync.sistema_cadena_suministro.domain.models.RelocationProposal;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class RelocationProposalVisitorTest {

    @Test
    void generatesProposalsForAItemsInDeepZones() {
        WarehouseNode warehouse = new WarehouseNode(1, "Main WH");
        ZoneNode zone = new ZoneNode(1, "Storage Zone");
        RackNode rack1 = new RackNode(1, "Rack Front");
        RackNode rack2 = new RackNode(2, "Rack Back");

        // Front location has 500 capacity, empty
        LocationNode frontLoc = new LocationNode(1, "Front Loc", 500, List.of());
        // Back location has an 'A' high-turnover product
        LocationNode backLoc = new LocationNode(2, "Back Loc", 500,
                List.of(new StoredItem(101, 50)));

        rack1.add(frontLoc);
        rack2.add(backLoc);
        zone.add(rack1);
        zone.add(rack2);
        warehouse.add(zone);

        RelocationProposalVisitor visitor = new RelocationProposalVisitor(Map.of(101, "A"));
        List<RelocationProposal> proposals = warehouse.accept(visitor);

        assertFalse(proposals.isEmpty());
        RelocationProposal p = proposals.get(0);
        assertEquals(101, p.productId());
        assertEquals(2, p.fromLocationId());
        assertEquals(1, p.toLocationId());
        assertEquals(50, p.quantity());
    }
}

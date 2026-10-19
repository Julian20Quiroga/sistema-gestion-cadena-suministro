package com.logisync.sistema_cadena_suministro.application.ports.driving;

import com.logisync.sistema_cadena_suministro.domain.storage.StoredItem;

import java.util.List;
import java.util.Map;

/** Read-only view of the stock stored per storage space. */
public interface StoredItemsReader {
    Map<Integer, List<StoredItem>> itemsBySpace(Integer warehouseId);

    long unitsInSpace(Integer spaceId);
}

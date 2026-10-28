package com.logisync.sistema_cadena_suministro.application.ports.driving;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.inventory.StockTotals;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository {
    Optional<InventoryItem> findById(Integer id);
    List<InventoryItem> findByIds(Collection<Integer> ids);
    Optional<InventoryItem> findBySpaceAndProduct(Integer storageSpaceId, Integer productId);
    InventoryItem findOrCreate(Integer storageSpaceId, Integer productId);
    List<InventoryItem> findCandidates(Integer warehouseId, Integer productId);
    PageResult<InventoryItem> search(Integer productId, Integer warehouseId, Integer storageSpaceId,
            PageQuery pageQuery);
    InventoryItem save(InventoryItem item);
    StockTotals totalsByProduct(Integer productId);
}

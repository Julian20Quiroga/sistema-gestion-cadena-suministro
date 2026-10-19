package com.logisync.sistema_cadena_suministro.infrastructure.persistence.adapters;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driving.InventoryRepository;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.inventory.StockTotals;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.InventoryDAO;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.PageSupport;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.StorageSpaceDAO;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.InventoryEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.StorageSpaceEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers.InventoryPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InventoryAdapterPersistence implements InventoryRepository {

    private final InventoryDAO inventoryDAO;
    private final StorageSpaceDAO storageSpaceDAO;
    private final InventoryPersistenceMapper mapper;

    @Override
    public Optional<InventoryItem> findById(Integer id) {
        return inventoryDAO.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<InventoryItem> findByIds(Collection<Integer> ids) {
        return inventoryDAO.findAllById(ids).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<InventoryItem> findBySpaceAndProduct(Integer storageSpaceId, Integer productId) {
        return inventoryDAO.findByStorageSpaceIdAndProductId(storageSpaceId, productId).map(mapper::toDomain);
    }

    @Override
    public InventoryItem findOrCreate(Integer storageSpaceId, Integer productId) {
        return findBySpaceAndProduct(storageSpaceId, productId)
                .orElseGet(() -> new InventoryItem(null, productId, storageSpaceId, 0, 0, null, null));
    }

    @Override
    public List<InventoryItem> findCandidates(Integer warehouseId, Integer productId) {
        return inventoryDAO.findCandidates(warehouseId, productId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public PageResult<InventoryItem> search(Integer productId, Integer warehouseId, Integer storageSpaceId,
            PageQuery pageQuery) {
        var page = inventoryDAO.search(productId, warehouseId, storageSpaceId, PageSupport.pageable(pageQuery));
        Map<Integer, Integer> warehouseBySpace = storageSpaceDAO
                .findAllById(page.getContent().stream().map(InventoryEntity::getStorageSpaceId).distinct().toList())
                .stream().collect(Collectors.toMap(StorageSpaceEntity::getId, StorageSpaceEntity::getWarehouseId));
        return PageSupport.toResult(page, entity -> {
            InventoryItem item = mapper.toDomain(entity);
            item.setWarehouseId(warehouseBySpace.get(entity.getStorageSpaceId()));
            return item;
        });
    }

    /** Persists the item and copies the generated id and version back to the domain instance. */
    @Override
    public InventoryItem save(InventoryItem item) {
        InventoryEntity entity = item.getId() == null ? mapper.toEntity(item)
                : inventoryDAO.findById(item.getId()).orElseThrow();
        if (item.getId() != null && item.getVersion() != null && !item.getVersion().equals(entity.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(InventoryEntity.class, item.getId());
        }
        entity.setPhysicalQuantity(item.getPhysicalQuantity());
        entity.setReservedQuantity(item.getReservedQuantity());
        InventoryEntity saved = inventoryDAO.saveAndFlush(entity);
        item.setId(saved.getId());
        item.setVersion(saved.getVersion());
        return item;
    }

    @Override
    public StockTotals totalsByProduct(Integer productId) {
        Object[] row = inventoryDAO.totals(productId).get(0);
        return new StockTotals(((Number) row[0]).longValue(), ((Number) row[1]).longValue());
    }

}

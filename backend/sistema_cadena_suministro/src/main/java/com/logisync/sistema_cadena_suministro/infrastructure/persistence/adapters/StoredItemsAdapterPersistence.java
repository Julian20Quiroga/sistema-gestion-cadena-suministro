package com.logisync.sistema_cadena_suministro.infrastructure.persistence.adapters;

import com.logisync.sistema_cadena_suministro.application.ports.driving.StoredItemsReader;
import com.logisync.sistema_cadena_suministro.domain.storage.StoredItem;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StoredItemsAdapterPersistence implements StoredItemsReader {

    private final EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public Map<Integer, List<StoredItem>> itemsBySpace(Integer warehouseId) {
        List<Object[]> rows = entityManager.createNativeQuery(
                "select i.storage_space_id, i.product_id, i.physical_quantity from inventory i "
                        + "join storage_spaces s on s.id = i.storage_space_id "
                        + "where s.warehouse_id = :warehouseId and i.physical_quantity > 0")
                .setParameter("warehouseId", warehouseId)
                .getResultList();
        Map<Integer, List<StoredItem>> items = new HashMap<>();
        for (Object[] row : rows) {
            items.computeIfAbsent(((Number) row[0]).intValue(), k -> new ArrayList<>())
                    .add(new StoredItem(((Number) row[1]).intValue(), ((Number) row[2]).longValue()));
        }
        return items;
    }

    @Override
    public long unitsInSpace(Integer spaceId) {
        Object result = entityManager.createNativeQuery(
                "select coalesce(sum(physical_quantity), 0) from inventory where storage_space_id = :spaceId")
                .setParameter("spaceId", spaceId)
                .getSingleResult();
        return ((Number) result).longValue();
    }
}

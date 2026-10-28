package com.logisync.sistema_cadena_suministro.application.service;

import com.logisync.sistema_cadena_suministro.application.inventory.LowStockMonitor;
import com.logisync.sistema_cadena_suministro.application.mappers.InventoryApplicationMapper;
import com.logisync.sistema_cadena_suministro.application.ports.driven.ReleaseStockUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.ReserveStockUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.InventoryRepository;
import com.logisync.sistema_cadena_suministro.application.responses.StockAllocationAppResponse;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.inventory.StockAllocation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class StockReservationService implements ReserveStockUseCase, ReleaseStockUseCase {

    private final InventoryRepository inventoryRepository;
    private final InventoryApplicationMapper mapper;
    private final LowStockMonitor lowStockMonitor;

    /** Reserves from the single location of the warehouse that fits best (smallest sufficient availability). */
    @Override
    @Transactional
    public StockAllocationAppResponse reserve(Integer warehouseId, Integer productId, int quantity) {
        if (quantity <= 0) {
            throw new BusinessRuleException("Quantity must be greater than zero");
        }
        InventoryItem item = inventoryRepository.findCandidates(warehouseId, productId).stream()
                .filter(candidate -> candidate.available() >= quantity)
                .min(Comparator.comparingInt(InventoryItem::available))
                .orElseThrow(() -> new BusinessRuleException("No single location in warehouse " + warehouseId
                        + " holds " + quantity + " available units of product " + productId));
        item.apply(0, quantity);
        inventoryRepository.save(item);
        lowStockMonitor.check(productId);
        return mapper.toResponse(new StockAllocation(item.getId(), item.getStorageSpaceId(), productId, quantity));
    }

    @Override
    @Transactional
    public void release(Integer inventoryId, int quantity) {
        InventoryItem item = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", inventoryId));
        item.apply(0, -quantity);
        inventoryRepository.save(item);
    }
}

package com.logisync.sistema_cadena_suministro.application.service;

import com.logisync.sistema_cadena_suministro.application.mappers.InventoryApplicationMapper;
import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryInventoryUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.InventoryRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.MovementRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.ProductRepository;
import com.logisync.sistema_cadena_suministro.application.responses.InventoryLevelAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.ProductAvailabilityAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.ProductRotationAppResponse;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.inventory.StockTotals;
import com.logisync.sistema_cadena_suministro.domain.models.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventoryQueryService implements QueryInventoryUseCase {

    private final InventoryRepository inventoryRepository;
    private final MovementRepository movementRepository;
    private final ProductRepository productRepository;
    private final InventoryApplicationMapper mapper;
    private final Clock clock;

    @Override
    @Transactional(readOnly = true)
    public PageResult<InventoryLevelAppResponse> findLevels(Integer productId, Integer warehouseId,
            Integer storageSpaceId, PageQuery pageQuery) {
        return inventoryRepository.search(productId, warehouseId, storageSpaceId, pageQuery).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductAvailabilityAppResponse availability(Integer productId) {
        Product product = requireProduct(productId);
        StockTotals totals = inventoryRepository.totalsByProduct(productId);
        int reorderPoint = product.getReorderPoint() == null ? 0 : product.getReorderPoint();
        return new ProductAvailabilityAppResponse(productId, totals.physical(), totals.reserved(),
                totals.available(), reorderPoint, reorderPoint > 0 && totals.available() <= reorderPoint);
    }

    /** Rotation index: units exited in the period divided by the current physical stock (minimum 1). */
    @Override
    @Transactional(readOnly = true)
    public ProductRotationAppResponse rotation(Integer productId, int days) {
        if (days <= 0) {
            throw new BusinessRuleException("The period must be at least one day");
        }
        requireProduct(productId);
        long exited = movementRepository.unitsExitedSince(productId, LocalDateTime.now(clock).minusDays(days));
        long physical = inventoryRepository.totalsByProduct(productId).physical();
        double index = Math.round(exited * 100.0 / Math.max(1, physical)) / 100.0;
        return new ProductRotationAppResponse(productId, days, exited, physical, index);
    }

    private Product requireProduct(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
    }
}

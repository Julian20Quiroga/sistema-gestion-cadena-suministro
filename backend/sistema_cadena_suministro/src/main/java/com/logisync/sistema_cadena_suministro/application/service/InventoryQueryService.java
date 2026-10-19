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

/**
 * Servicio de aplicación para consultas especializadas de balances de stock, disponibilidad y rotación.
 */
@Service
@RequiredArgsConstructor
public class InventoryQueryService implements QueryInventoryUseCase {

    private final InventoryRepository inventoryRepository;
    private final MovementRepository movementRepository;
    private final ProductRepository productRepository;
    private final InventoryApplicationMapper mapper;
    private final Clock clock;

    /**
     * Consulta los niveles de inventario desglosados por producto, almacén o espacio de almacenamiento específico.
     */
    @Override
    @Transactional(readOnly = true)
    public PageResult<InventoryLevelAppResponse> findLevels(Integer productId, Integer warehouseId,
            Integer storageSpaceId, PageQuery pageQuery) {
        return inventoryRepository.search(productId, warehouseId, storageSpaceId, pageQuery).map(mapper::toResponse);
    }

    /**
     * Calcula la disponibilidad global consolidada de un producto (físico, reservado y disponible neto)
     * e indica si se encuentra por debajo del punto de reorden.
     */
    @Override
    @Transactional(readOnly = true)
    public ProductAvailabilityAppResponse availability(Integer productId) {
        Product product = requireProduct(productId);
        StockTotals totals = inventoryRepository.totalsByProduct(productId);
        int reorderPoint = product.getReorderPoint() == null ? 0 : product.getReorderPoint();
        return new ProductAvailabilityAppResponse(productId, totals.physical(), totals.reserved(),
                totals.available(), reorderPoint, reorderPoint > 0 && totals.available() <= reorderPoint);
    }

    /**
     * Calcula el índice de rotación de un producto en un período de días determinado
     * (unidades despachadas divididas entre el stock físico actual).
     */
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

    /**
     * Valida la existencia del producto en el catálogo.
     */
    private Product requireProduct(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
    }
}

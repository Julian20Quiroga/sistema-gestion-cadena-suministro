package com.logisync.sistema_cadena_suministro.application.inventory;

import com.logisync.sistema_cadena_suministro.application.events.StockLowEvent;
import com.logisync.sistema_cadena_suministro.application.ports.driving.DomainEventPublisher;
import com.logisync.sistema_cadena_suministro.application.ports.driving.InventoryRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.ProductRepository;
import com.logisync.sistema_cadena_suministro.domain.models.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Monitor reactivo de niveles de stock para la detección de umbrales críticos.
 * Patrón aplicado: Observer (actúa como emisor / sujeto del evento StockLowEvent cuando
 * el balance disponible desciende por debajo del punto de reorden configurado).
 */
@Component
@RequiredArgsConstructor
public class LowStockMonitor {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final DomainEventPublisher eventPublisher;

    /**
     * Evalúa el balance disponible actual de un producto contra su punto de reorden.
     * Patrón aplicado: Observer (si el stock está en o por debajo del umbral, publica el evento reactivo).
     */
    public void check(Integer productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null || product.getReorderPoint() == null || product.getReorderPoint() <= 0) {
            return;
        }
        long available = inventoryRepository.totalsByProduct(productId).available();
        if (available <= product.getReorderPoint()) {
            eventPublisher.publish(new StockLowEvent(productId, available, product.getReorderPoint()));
        }
    }
}

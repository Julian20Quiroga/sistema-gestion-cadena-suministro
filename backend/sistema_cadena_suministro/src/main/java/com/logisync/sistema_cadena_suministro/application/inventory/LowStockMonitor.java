package com.logisync.sistema_cadena_suministro.application.inventory;

import com.logisync.sistema_cadena_suministro.application.events.StockLowEvent;
import com.logisync.sistema_cadena_suministro.application.ports.driving.DomainEventPublisher;
import com.logisync.sistema_cadena_suministro.application.ports.driving.InventoryRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.ProductRepository;
import com.logisync.sistema_cadena_suministro.domain.models.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Publishes a {@link StockLowEvent} when the available stock of a product reaches its reorder point. */
@Component
@RequiredArgsConstructor
public class LowStockMonitor {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final DomainEventPublisher eventPublisher;

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

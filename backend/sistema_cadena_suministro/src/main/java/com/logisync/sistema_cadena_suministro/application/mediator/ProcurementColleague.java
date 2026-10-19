package com.logisync.sistema_cadena_suministro.application.mediator;

import com.logisync.sistema_cadena_suministro.application.ports.driving.SupplyNeedRepository;
import com.logisync.sistema_cadena_suministro.domain.enums.SupplyNeedOrigin;
import com.logisync.sistema_cadena_suministro.domain.enums.SupplyNeedStatus;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrder;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrderDetail;
import com.logisync.sistema_cadena_suministro.domain.models.SupplyNeed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mediator: Colleague responsible for procurement reactions to domain events.
 */
@Slf4j
@Component
public class ProcurementColleague extends Colleague {

    private final SupplyNeedRepository supplyNeedRepository;

    public ProcurementColleague(SupplyChainMediator mediator, SupplyNeedRepository supplyNeedRepository) {
        super(mediator);
        this.supplyNeedRepository = supplyNeedRepository;
    }

    public void handleStockLow(Integer productId, long availableStock, int reorderPoint) {
        boolean alreadyOpen = supplyNeedRepository.existsOpenByProduct(productId);
        if (alreadyOpen) {
            log.info("Supply need for product {} already open, skipping creation", productId);
            return;
        }
        int needed = Math.max(reorderPoint * 2 - (int) availableStock, reorderPoint);
        SupplyNeed need = new SupplyNeed(null, productId, needed, SupplyNeedOrigin.LOW_STOCK,
                SupplyNeedStatus.OPEN, null, null, null);
        supplyNeedRepository.save(need);
        log.info("Created supply need for product {} with quantity {}", productId, needed);
    }

    public void handlePurchaseOrderCreated(PurchaseOrder order) {
        List<Integer> productIds = order.getDetails().stream().map(PurchaseOrderDetail::getProductId).toList();
        for (Integer prodId : productIds) {
            supplyNeedRepository.findOpenByProduct(prodId).ifPresent(need -> {
                need.cover(order.getId());
                supplyNeedRepository.save(need);
                log.info("Covered supply need {} with purchase order {}", need.getId(), order.getId());
            });
        }
    }
}

package com.logisync.sistema_cadena_suministro.application.mediator;

import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrder;

/**
 * Mediator: Decouples communication between inventory, procurement, and future order modules.
 */
public interface SupplyChainMediator {
    void registerColleague(Colleague colleague);
    void notifyStockLow(Integer productId, long availableStock, int reorderPoint);
    void notifyPurchaseOrderCreated(PurchaseOrder order);
}

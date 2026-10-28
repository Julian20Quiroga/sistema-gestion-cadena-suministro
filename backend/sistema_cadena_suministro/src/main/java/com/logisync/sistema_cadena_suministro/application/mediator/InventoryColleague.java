package com.logisync.sistema_cadena_suministro.application.mediator;

import org.springframework.stereotype.Component;

/**
 * Mediator: Colleague representing the inventory subsystem.
 */
@Component
public class InventoryColleague extends Colleague {

    public InventoryColleague(SupplyChainMediator mediator) {
        super(mediator);
    }

    public void onStockLow(Integer productId, long availableStock, int reorderPoint) {
        mediator.notifyStockLow(productId, availableStock, reorderPoint);
    }
}

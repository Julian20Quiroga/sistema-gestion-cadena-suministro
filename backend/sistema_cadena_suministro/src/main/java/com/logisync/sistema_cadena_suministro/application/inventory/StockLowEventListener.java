package com.logisync.sistema_cadena_suministro.application.inventory;

import com.logisync.sistema_cadena_suministro.application.events.DomainEventSubscriber;
import com.logisync.sistema_cadena_suministro.application.events.StockLowEvent;
import com.logisync.sistema_cadena_suministro.application.mediator.InventoryColleague;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Observer subscriber: Listens to stock low events and forwards them to the mediator via colleague.
 */
@Component
@RequiredArgsConstructor
public class StockLowEventListener implements DomainEventSubscriber<StockLowEvent> {

    private final InventoryColleague inventoryColleague;

    @Override
    public Class<StockLowEvent> eventType() {
        return StockLowEvent.class;
    }

    @Override
    public void onEvent(StockLowEvent event) {
        inventoryColleague.onStockLow(event.productId(), event.availableQuantity(), event.reorderPoint());
    }
}

package com.logisync.sistema_cadena_suministro.application.facade;

import com.logisync.sistema_cadena_suministro.application.commands.CreateOrderCommand;
import com.logisync.sistema_cadena_suministro.application.commands.CreatePurchaseOrderCommand;
import com.logisync.sistema_cadena_suministro.application.responses.OrderAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.PurchaseOrderAppResponse;
import com.logisync.sistema_cadena_suministro.domain.inventory.StockTotals;

/**
 * Facade: Provides a unified, high-level interface to complex subsystems (orders, inventory, procurement).
 */
public interface SupplyChainOperationsFacade {
    OrderAppResponse placeCustomerOrder(CreateOrderCommand command);
    PurchaseOrderAppResponse procureAndReceiveMerchandise(CreatePurchaseOrderCommand command, Integer storageSpaceId);
    StockTotals getConsolidatedStock(Integer productId);
}

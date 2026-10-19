package com.logisync.sistema_cadena_suministro.domain.procurement.state;

import com.logisync.sistema_cadena_suministro.domain.enums.PurchaseOrderStatus;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrder;

/**
 * State: Encapsulates state-specific behavior and transitions for PurchaseOrder.
 */
public interface PurchaseOrderState {
    PurchaseOrderStatus getStatus();
    void receive(PurchaseOrder order, int totalReceivedSoFar, int totalOrdered);
    void cancel(PurchaseOrder order);
}

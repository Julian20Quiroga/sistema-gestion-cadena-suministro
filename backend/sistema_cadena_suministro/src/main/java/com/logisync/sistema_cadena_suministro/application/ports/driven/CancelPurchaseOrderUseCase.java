package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.responses.PurchaseOrderAppResponse;

public interface CancelPurchaseOrderUseCase {
    PurchaseOrderAppResponse cancel(Integer purchaseOrderId);
}

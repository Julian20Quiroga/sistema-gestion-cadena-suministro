package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.CreatePurchaseOrderCommand;
import com.logisync.sistema_cadena_suministro.application.responses.PurchaseOrderAppResponse;

public interface CreatePurchaseOrderUseCase {
    PurchaseOrderAppResponse create(CreatePurchaseOrderCommand command);
}

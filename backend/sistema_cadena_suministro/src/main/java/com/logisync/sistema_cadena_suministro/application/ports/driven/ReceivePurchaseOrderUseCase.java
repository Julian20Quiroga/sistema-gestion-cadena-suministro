package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.ReceivePurchaseOrderCommand;
import com.logisync.sistema_cadena_suministro.application.responses.PurchaseOrderAppResponse;

public interface ReceivePurchaseOrderUseCase {
    PurchaseOrderAppResponse receive(ReceivePurchaseOrderCommand command);
}

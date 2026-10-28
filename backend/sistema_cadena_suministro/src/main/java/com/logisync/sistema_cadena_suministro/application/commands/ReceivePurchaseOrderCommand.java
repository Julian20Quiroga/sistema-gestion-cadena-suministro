package com.logisync.sistema_cadena_suministro.application.commands;

import java.util.List;

public record ReceivePurchaseOrderCommand(
        Integer purchaseOrderId,
        List<ReceptionDetail> items
) {
    public record ReceptionDetail(Integer productId, Integer storageSpaceId, int quantity) {}
}

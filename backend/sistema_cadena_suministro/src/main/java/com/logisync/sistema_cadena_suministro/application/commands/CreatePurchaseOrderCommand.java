package com.logisync.sistema_cadena_suministro.application.commands;

import java.math.BigDecimal;
import java.util.List;

public record CreatePurchaseOrderCommand(
        Integer supplierId,
        Integer warehouseId,
        List<DetailItem> items
) {
    public record DetailItem(Integer productId, int quantity, BigDecimal unitPrice) {}
}

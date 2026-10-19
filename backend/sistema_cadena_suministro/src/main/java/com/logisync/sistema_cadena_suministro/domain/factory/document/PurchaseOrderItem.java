package com.logisync.sistema_cadena_suministro.domain.factory.document;

import java.math.BigDecimal;

public record PurchaseOrderItem(Integer productId, int quantity, BigDecimal unitPrice) implements DocumentItem {
    @Override
    public Integer getProductId() { return productId; }
    @Override
    public int getQuantity() { return quantity; }
    @Override
    public BigDecimal getUnitPrice() { return unitPrice; }
    @Override
    public BigDecimal getSubtotal() { return unitPrice.multiply(BigDecimal.valueOf(quantity)); }
}

package com.logisync.sistema_cadena_suministro.domain.factory.document;

import java.math.BigDecimal;

public record TransferOrderItem(Integer productId, int quantity) implements DocumentItem {
    @Override
    public Integer getProductId() { return productId; }
    @Override
    public int getQuantity() { return quantity; }
    @Override
    public BigDecimal getUnitPrice() { return BigDecimal.ZERO; }
    @Override
    public BigDecimal getSubtotal() { return BigDecimal.ZERO; }
}

package com.logisync.sistema_cadena_suministro.domain.factory.document;

import java.math.BigDecimal;

public interface DocumentItem {
    Integer getProductId();
    int getQuantity();
    BigDecimal getUnitPrice();
    BigDecimal getSubtotal();
}

package com.logisync.sistema_cadena_suministro.domain.factory.document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Abstract Factory: Concrete factory creating Purchase Order document families.
 */
public class PurchaseOrderDocumentFactory implements SupplyChainDocumentFactory {

    @Override
    public DocumentHeader createHeader(String reference, Integer supplierId) {
        return new PurchaseOrderHeader(reference, supplierId, LocalDateTime.now());
    }

    @Override
    public DocumentItem createItem(Integer productId, int quantity, BigDecimal unitPrice) {
        return new PurchaseOrderItem(productId, quantity, unitPrice);
    }
}

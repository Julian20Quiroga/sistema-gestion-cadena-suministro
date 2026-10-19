package com.logisync.sistema_cadena_suministro.domain.factory.document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Abstract Factory: Concrete factory creating Internal Warehouse Transfer document families.
 */
public class InternalTransferDocumentFactory implements SupplyChainDocumentFactory {

    private final Integer targetWarehouseId;

    public InternalTransferDocumentFactory(Integer targetWarehouseId) {
        this.targetWarehouseId = targetWarehouseId;
    }

    @Override
    public DocumentHeader createHeader(String reference, Integer sourceWarehouseId) {
        return new TransferOrderHeader(reference, sourceWarehouseId, targetWarehouseId, LocalDateTime.now());
    }

    @Override
    public DocumentItem createItem(Integer productId, int quantity, BigDecimal unitPrice) {
        return new TransferOrderItem(productId, quantity);
    }
}

package com.logisync.sistema_cadena_suministro.domain.factory.document;

import java.math.BigDecimal;

/**
 * Abstract Factory: Declares creation methods for families of supply chain documents (Headers and Items).
 */
public interface SupplyChainDocumentFactory {
    DocumentHeader createHeader(String reference, Integer partyId);
    DocumentItem createItem(Integer productId, int quantity, BigDecimal unitPrice);
}

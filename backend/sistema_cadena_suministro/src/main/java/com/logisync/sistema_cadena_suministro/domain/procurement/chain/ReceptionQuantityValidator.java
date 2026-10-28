package com.logisync.sistema_cadena_suministro.domain.procurement.chain;

import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrderDetail;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Chain of Responsibility: Validates received quantities do not exceed remaining ordered quantities.
 */
public class ReceptionQuantityValidator extends ReceptionValidator {

    @Override
    protected void check(ReceptionValidationContext context) {
        if (context.items().isEmpty()) {
            throw new BusinessRuleException("Reception must contain at least one item");
        }
        Map<Integer, Integer> receivedByProduct = context.items().stream()
                .collect(Collectors.groupingBy(ReceptionItem::productId, Collectors.summingInt(ReceptionItem::quantity)));

        Map<Integer, PurchaseOrderDetail> detailByProduct = context.order().getDetails().stream()
                .collect(Collectors.toMap(PurchaseOrderDetail::getProductId, d -> d));

        for (var entry : receivedByProduct.entrySet()) {
            Integer prodId = entry.getKey();
            int qty = entry.getValue();
            if (qty <= 0) {
                throw new BusinessRuleException("Received quantity must be positive for product " + prodId);
            }
            PurchaseOrderDetail detail = detailByProduct.get(prodId);
            if (detail == null) {
                throw new BusinessRuleException("Product " + prodId + " is not part of purchase order "
                        + context.order().getId());
            }
            if (qty > detail.remainingQuantity()) {
                throw new BusinessRuleException("Received quantity " + qty + " exceeds remaining ordered quantity "
                        + detail.remainingQuantity() + " for product " + prodId);
            }
        }
    }
}

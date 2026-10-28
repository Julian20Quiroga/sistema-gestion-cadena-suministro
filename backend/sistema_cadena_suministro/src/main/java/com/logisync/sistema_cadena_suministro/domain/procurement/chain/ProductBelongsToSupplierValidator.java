package com.logisync.sistema_cadena_suministro.domain.procurement.chain;

import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.models.Product;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrderDetail;

/**
 * Chain of Responsibility: Validates products exist, are active, and belong to the chosen supplier.
 */
public class ProductBelongsToSupplierValidator extends CreationOrderValidator {

    @Override
    protected void check(CreationValidationContext context) {
        if (context.order().getDetails().isEmpty()) {
            throw new BusinessRuleException("Purchase order must have at least one detail line");
        }
        for (PurchaseOrderDetail detail : context.order().getDetails()) {
            Product product = context.productsById().get(detail.getProductId());
            if (product == null || !product.isActive()) {
                throw new BusinessRuleException("Product " + detail.getProductId() + " is missing or inactive");
            }
            if (!product.getSupplierId().equals(context.supplier().getId())) {
                throw new BusinessRuleException("Product " + product.getId() + " does not belong to supplier "
                        + context.supplier().getId());
            }
            if (detail.getQuantity() <= 0) {
                throw new BusinessRuleException("Quantity must be positive for product " + product.getId());
            }
        }
    }
}

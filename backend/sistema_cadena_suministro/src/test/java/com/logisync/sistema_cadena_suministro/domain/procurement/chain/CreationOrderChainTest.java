package com.logisync.sistema_cadena_suministro.domain.procurement.chain;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.models.Product;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrder;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrderDetail;
import com.logisync.sistema_cadena_suministro.domain.models.Supplier;
import com.logisync.sistema_cadena_suministro.domain.models.Warehouse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreationOrderChainTest {

    @Test
    void validatesSupplierWarehouseAndProductBelonging() {
        Supplier supplier = new Supplier(1, "123", "Acme", "300", "a@a.co", SoftDeleteStatus.ACTIVE);
        Warehouse warehouse = new Warehouse(10, "W1", "Addr", "300", SoftDeleteStatus.ACTIVE, 1, null, null);
        Product product = new Product();
        product.setId(100);
        product.setSupplierId(1);
        product.setStatus(SoftDeleteStatus.ACTIVE);

        PurchaseOrder order = new PurchaseOrder();
        order.setSupplierId(1);
        order.setWarehouseId(10);
        order.setDetails(List.of(new PurchaseOrderDetail(null, 100, null, 5, 0, new BigDecimal("10.00"))));

        CreationOrderValidator chain = new SupplierActiveValidator();
        chain.linkWith(new WarehouseActiveValidator())
             .linkWith(new ProductBelongsToSupplierValidator());

        assertDoesNotThrow(() -> chain.validate(new CreationValidationContext(order, supplier, warehouse, Map.of(100, product))));

        // Product from different supplier throws
        product.setSupplierId(999);
        assertThrows(BusinessRuleException.class, () -> chain.validate(new CreationValidationContext(order, supplier, warehouse, Map.of(100, product))));
    }
}

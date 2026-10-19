package com.logisync.sistema_cadena_suministro.domain.orders.chain;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.models.Order;
import com.logisync.sistema_cadena_suministro.domain.models.OrderDetail;
import com.logisync.sistema_cadena_suministro.domain.models.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderValidationChainTest {

    @Test
    void validatesOrderDataAndSufficientStock() {
        Product p = new Product();
        p.setId(10);
        p.setStatus(SoftDeleteStatus.ACTIVE);

        Order order = new Order();
        order.setOrderReference("ORD-101");
        order.setCustomerName("John Doe");
        order.setDeliveryAddress("Street 1");
        order.setCustomerPhone("555-1234");
        order.setDetails(List.of(new OrderDetail(null, 10, null, 5, new BigDecimal("20.00"))));

        OrderCreationValidator chain = new OrderCustomerDataValidator();
        chain.linkWith(new OrderProductsActiveValidator())
             .linkWith(new OrderStockAvailabilityValidator());

        // Stock available = 10 -> passes
        assertDoesNotThrow(() -> chain.validate(new OrderCreationContext(order, Map.of(10, p), Map.of(10, 10L))));

        // Stock available = 2 -> fails
        assertThrows(BusinessRuleException.class,
                () -> chain.validate(new OrderCreationContext(order, Map.of(10, p), Map.of(10, 2L))));
    }
}

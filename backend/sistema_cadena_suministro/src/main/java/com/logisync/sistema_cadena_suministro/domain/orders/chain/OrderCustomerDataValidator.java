package com.logisync.sistema_cadena_suministro.domain.orders.chain;

import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.models.Order;

import java.math.BigDecimal;

/**
 * Chain of Responsibility: Validates customer information and destination coordinates.
 */
public class OrderCustomerDataValidator extends OrderCreationValidator {

    @Override
    protected void check(OrderCreationContext context) {
        Order order = context.order();
        if (order.getOrderReference() == null || order.getOrderReference().isBlank()) {
            throw new BusinessRuleException("Order reference is required");
        }
        if (order.getCustomerName() == null || order.getCustomerName().isBlank()) {
            throw new BusinessRuleException("Customer name is required");
        }
        if (order.getDeliveryAddress() == null || order.getDeliveryAddress().isBlank()) {
            throw new BusinessRuleException("Delivery address is required");
        }
        if (order.getCustomerPhone() == null || order.getCustomerPhone().isBlank()) {
            throw new BusinessRuleException("Customer phone is required");
        }
        if (order.getDeliveryLatitude() != null) {
            if (order.getDeliveryLatitude().compareTo(new BigDecimal("-90")) < 0
                    || order.getDeliveryLatitude().compareTo(new BigDecimal("90")) > 0) {
                throw new BusinessRuleException("Latitude must be between -90 and 90");
            }
        }
        if (order.getDeliveryLongitude() != null) {
            if (order.getDeliveryLongitude().compareTo(new BigDecimal("-180")) < 0
                    || order.getDeliveryLongitude().compareTo(new BigDecimal("180")) > 0) {
                throw new BusinessRuleException("Longitude must be between -180 and 180");
            }
        }
    }
}

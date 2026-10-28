package com.logisync.sistema_cadena_suministro.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDetail {
    private Integer id;
    private Integer productId;
    private Integer purchaseOrderId;
    private int quantity;
    private int receivedQuantity;
    private BigDecimal unitPrice;

    public int remainingQuantity() {
        return Math.max(0, quantity - receivedQuantity);
    }

    public void addReceived(int qty) {
        this.receivedQuantity += qty;
    }
}

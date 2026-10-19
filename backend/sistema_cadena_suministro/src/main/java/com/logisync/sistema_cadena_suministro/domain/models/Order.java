package com.logisync.sistema_cadena_suministro.domain.models;

import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;
import com.logisync.sistema_cadena_suministro.domain.orders.state.OrderState;
import com.logisync.sistema_cadena_suministro.domain.orders.state.OrderStateFactory;
import com.logisync.sistema_cadena_suministro.domain.orders.state.RegisteredOrderState;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Order {
    private Integer id;
    private String orderReference;
    private String deliveryAddress;
    private String deliveryCity;
    private String deliveryCountry;
    private String customerPhone;
    private String customerName;
    private BigDecimal deliveryLatitude;
    private BigDecimal deliveryLongitude;
    private OrderStatus status;
    private List<OrderDetail> details = new ArrayList<>();

    private OrderState state;

    public Order() {
        changeState(new RegisteredOrderState());
    }

    public Order(Integer id, String orderReference, String deliveryAddress, String deliveryCity,
                 String deliveryCountry, String customerPhone, String customerName,
                 BigDecimal deliveryLatitude, BigDecimal deliveryLongitude,
                 OrderStatus status, List<OrderDetail> details) {
        this.id = id;
        this.orderReference = orderReference;
        this.deliveryAddress = deliveryAddress;
        this.deliveryCity = deliveryCity;
        this.deliveryCountry = deliveryCountry;
        this.customerPhone = customerPhone;
        this.customerName = customerName;
        this.deliveryLatitude = deliveryLatitude;
        this.deliveryLongitude = deliveryLongitude;
        this.details = details != null ? details : new ArrayList<>();
        changeState(OrderStateFactory.from(status));
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
        this.state = OrderStateFactory.from(status);
    }

    public void changeState(OrderState newState) {
        this.state = newState;
        this.status = newState.getStatus();
    }

    public void startPreparation() {
        this.state.startPreparation(this);
    }

    public void markDispatched() {
        this.state.markDispatched(this);
    }

    public void markDelivered() {
        this.state.markDelivered(this);
    }

    public void cancel() {
        this.state.cancel(this);
    }

    public BigDecimal totalAmount() {
        return details.stream()
                .map(OrderDetail::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

package com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities;

import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity extends BaseEntity {

    @Column(name = "order_reference", nullable = false, unique = true, length = 100)
    private String orderReference;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "delivery_city", nullable = false, length = 100)
    private String deliveryCity;

    @Column(name = "delivery_country", nullable = false, length = 100)
    private String deliveryCountry;

    @Column(name = "customer_phone", nullable = false, length = 20)
    private String customerPhone;

    @Column(name = "customer_name", nullable = false, length = 150)
    private String customerName;

    @Column(name = "delivery_latitude", precision = 9, scale = 6)
    private BigDecimal deliveryLatitude;

    @Column(name = "delivery_longitude", precision = 9, scale = 6)
    private BigDecimal deliveryLongitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderDetailEntity> details = new ArrayList<>();
}

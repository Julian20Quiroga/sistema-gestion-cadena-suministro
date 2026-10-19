package com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities;

import com.logisync.sistema_cadena_suministro.domain.enums.SupplyNeedOrigin;
import com.logisync.sistema_cadena_suministro.domain.enums.SupplyNeedStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "supply_needs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplyNeedEntity extends BaseEntity {

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "requested_quantity", nullable = false)
    private Integer requestedQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "origin", nullable = false)
    private SupplyNeedOrigin origin;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SupplyNeedStatus status;

    @Column(name = "purchase_order_id")
    private Integer purchaseOrderId;
}

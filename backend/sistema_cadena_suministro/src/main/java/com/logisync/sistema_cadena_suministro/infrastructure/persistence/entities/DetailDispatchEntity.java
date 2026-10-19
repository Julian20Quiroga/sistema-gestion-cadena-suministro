package com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "detail_dispatches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetailDispatchEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_id", nullable = false)
    private DispatchEntity dispatch;

    @Column(name = "detail_order_id", nullable = false, unique = true)
    private Integer detailOrderId;

    @Column(name = "storage_space_id", nullable = false)
    private Integer storageSpaceId;

    @Column(name = "inventory_id", nullable = false)
    private Integer inventoryId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}

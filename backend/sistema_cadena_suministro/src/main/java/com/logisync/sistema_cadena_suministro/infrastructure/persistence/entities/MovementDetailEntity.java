package com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "detail_inventory_movements")
@Getter
@Setter
public class MovementDetailEntity extends BaseEntity {

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "reserved_delta")
    private Integer reservedDelta;

    @Column(name = "inventory_id")
    private Integer inventoryId;
}

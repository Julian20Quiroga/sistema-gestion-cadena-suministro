package com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "inventory")
@Getter
@Setter
public class InventoryEntity extends BaseEntity {

    @Column(name = "physical_quantity")
    private Integer physicalQuantity;

    @Column(name = "reserved_quantity")
    private Integer reservedQuantity;

    @Column(name = "storage_space_id")
    private Integer storageSpaceId;

    @Column(name = "product_id")
    private Integer productId;

    @Version
    @Column(name = "version")
    private Integer version;
}

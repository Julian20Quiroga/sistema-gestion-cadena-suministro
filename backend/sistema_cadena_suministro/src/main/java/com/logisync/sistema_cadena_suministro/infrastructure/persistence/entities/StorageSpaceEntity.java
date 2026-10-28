package com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "storage_spaces")
@Getter
@Setter
public class StorageSpaceEntity extends BaseEntity {

    @Column(name = "storage_space_name")
    private String name;

    @Column(name = "warehouse_id")
    private Integer warehouseId;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "storage_space_status")
    @Enumerated(EnumType.STRING)
    private SoftDeleteStatus status;

    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "space_type")
    @Enumerated(EnumType.STRING)
    private StorageSpaceType type;
}

package com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "layout_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LayoutSnapshotEntity extends BaseEntity {

    @Column(name = "warehouse_id", nullable = false)
    private Integer warehouseId;

    @Column(name = "snapshot_name", nullable = false, length = 150)
    private String snapshotName;

    @Lob
    @Column(name = "layout_data_json", nullable = false, columnDefinition = "LONGTEXT")
    private String layoutDataJson;
}

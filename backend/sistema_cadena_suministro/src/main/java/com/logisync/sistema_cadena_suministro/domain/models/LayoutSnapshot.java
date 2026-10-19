package com.logisync.sistema_cadena_suministro.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LayoutSnapshot {
    private Integer id;
    private Integer warehouseId;
    private String snapshotName;
    private String layoutDataJson;
    private LocalDateTime createdAt;
}

package com.logisync.sistema_cadena_suministro.application.ports.driving;

import com.logisync.sistema_cadena_suministro.domain.models.LayoutSnapshot;

import java.util.List;
import java.util.Optional;

public interface LayoutSnapshotRepository {
    LayoutSnapshot save(LayoutSnapshot snapshot);
    Optional<LayoutSnapshot> findById(Integer id);
    List<LayoutSnapshot> findByWarehouseId(Integer warehouseId);
}

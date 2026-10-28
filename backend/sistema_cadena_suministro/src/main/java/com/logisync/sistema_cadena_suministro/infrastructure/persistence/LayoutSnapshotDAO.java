package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.LayoutSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LayoutSnapshotDAO extends JpaRepository<LayoutSnapshotEntity, Integer> {
    List<LayoutSnapshotEntity> findByWarehouseIdOrderByCreatedDateDesc(Integer warehouseId);
}

package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.StorageSpaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StorageSpaceDAO extends JpaRepository<StorageSpaceEntity, Integer> {
    List<StorageSpaceEntity> findByWarehouseIdAndStatus(Integer warehouseId, SoftDeleteStatus status);

    boolean existsByParentIdAndStatus(Integer parentId, SoftDeleteStatus status);
}

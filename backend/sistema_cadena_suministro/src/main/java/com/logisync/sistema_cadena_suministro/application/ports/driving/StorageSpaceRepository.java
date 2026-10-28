package com.logisync.sistema_cadena_suministro.application.ports.driving;

import com.logisync.sistema_cadena_suministro.domain.models.StorageSpace;

import java.util.List;
import java.util.Optional;

public interface StorageSpaceRepository {
    StorageSpace create(StorageSpace space);
    StorageSpace update(StorageSpace space);
    void delete(Integer id);
    Optional<StorageSpace> findById(Integer id);
    List<StorageSpace> findActiveByWarehouse(Integer warehouseId);
    boolean hasActiveChildren(Integer id);
}

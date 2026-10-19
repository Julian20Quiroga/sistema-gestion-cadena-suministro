package com.logisync.sistema_cadena_suministro.application.ports.driving;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.models.Warehouse;

import java.util.Optional;

public interface WarehouseRepository {
    Warehouse create(Warehouse warehouse);
    Warehouse update(Warehouse warehouse, Integer id);
    void delete(Integer id);
    Optional<Warehouse> findById(Integer id);
    PageResult<Warehouse> findAll(String name, SoftDeleteStatus status, PageQuery pageQuery);
}

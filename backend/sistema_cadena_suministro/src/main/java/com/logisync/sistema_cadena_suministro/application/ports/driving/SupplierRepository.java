package com.logisync.sistema_cadena_suministro.application.ports.driving;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.models.Supplier;

import java.util.Optional;

public interface SupplierRepository {
    Supplier create(Supplier supplier);
    Supplier update(Supplier supplier, Integer id);
    void delete(Integer id);
    Optional<Supplier> findById(Integer id);
    PageResult<Supplier> findAll(String name, SoftDeleteStatus status, PageQuery pageQuery);
}

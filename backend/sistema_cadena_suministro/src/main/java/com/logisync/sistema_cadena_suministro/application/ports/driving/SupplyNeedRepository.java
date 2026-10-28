package com.logisync.sistema_cadena_suministro.application.ports.driving;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.domain.enums.SupplyNeedStatus;
import com.logisync.sistema_cadena_suministro.domain.models.SupplyNeed;

import java.util.Optional;

public interface SupplyNeedRepository {
    SupplyNeed save(SupplyNeed supplyNeed);
    Optional<SupplyNeed> findById(Integer id);
    boolean existsOpenByProduct(Integer productId);
    Optional<SupplyNeed> findOpenByProduct(Integer productId);
    PageResult<SupplyNeed> findAll(SupplyNeedStatus status, PageQuery pageQuery);
}

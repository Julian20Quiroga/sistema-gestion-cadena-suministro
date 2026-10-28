package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.WarehouseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WarehouseDAO extends JpaRepository<WarehouseEntity, Integer> {

    @Query("select w from WarehouseEntity w where lower(w.name) like :pattern "
            + "and (:status is null or w.status = :status)")
    Page<WarehouseEntity> search(@Param("pattern") String pattern, @Param("status") SoftDeleteStatus status,
            Pageable pageable);
}

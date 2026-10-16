package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.SupplierEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SupplierDAO extends JpaRepository<SupplierEntity, Integer> {

    @Query("select s from SupplierEntity s where lower(s.name) like :pattern "
            + "and (:status is null or s.status = :status)")
    Page<SupplierEntity> search(@Param("pattern") String pattern, @Param("status") SoftDeleteStatus status,
            Pageable pageable);
}

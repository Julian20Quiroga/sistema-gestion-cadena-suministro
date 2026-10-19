package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.domain.enums.DispatchStatus;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.DispatchEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DispatchDAO extends JpaRepository<DispatchEntity, Integer> {

    boolean existsByOrderId(Integer orderId);

    @Query("""
        SELECT d FROM DispatchEntity d
        WHERE (:warehouseId IS NULL OR d.warehouseId = :warehouseId)
          AND (:orderId IS NULL OR d.orderId = :orderId)
          AND (:status IS NULL OR d.status = :status)
        ORDER BY d.id DESC
    """)
    Page<DispatchEntity> search(
            @Param("warehouseId") Integer warehouseId,
            @Param("orderId") Integer orderId,
            @Param("status") DispatchStatus status,
            Pageable pageable
    );
}

package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.domain.enums.PurchaseOrderStatus;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.PurchaseOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderDAO extends JpaRepository<PurchaseOrderEntity, Integer> {

    boolean existsBySupplierId(Integer supplierId);

    @Query("""
        SELECT po FROM PurchaseOrderEntity po
        WHERE (:supplierId IS NULL OR po.supplierId = :supplierId)
          AND (:warehouseId IS NULL OR po.warehouseId = :warehouseId)
          AND (:status IS NULL OR po.status = :status)
        ORDER BY po.id DESC
    """)
    Page<PurchaseOrderEntity> search(
            @Param("supplierId") Integer supplierId,
            @Param("warehouseId") Integer warehouseId,
            @Param("status") PurchaseOrderStatus status,
            Pageable pageable
    );
}


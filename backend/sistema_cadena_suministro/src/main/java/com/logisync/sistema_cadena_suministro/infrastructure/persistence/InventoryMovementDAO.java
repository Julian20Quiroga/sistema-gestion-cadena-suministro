package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.domain.inventory.MovementStatus;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.InventoryMovementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface InventoryMovementDAO extends JpaRepository<InventoryMovementEntity, Integer> {

    @Query("select distinct m from InventoryMovementEntity m join m.details d "
            + "where (:type is null or m.movementType = :type) "
            + "and m.movementDate >= :from and m.movementDate < :to "
            + "and (:productId is null or d.inventoryId in "
            + "(select i.id from InventoryEntity i where i.productId = :productId))")
    Page<InventoryMovementEntity> search(@Param("productId") Integer productId, @Param("type") String type,
            @Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);

    @Query("select coalesce(-sum(d.quantity), 0L) from InventoryMovementEntity m join m.details d "
            + "where m.movementType = 'SALIDA' and m.status = :status and m.movementDate >= :since "
            + "and d.inventoryId in (select i.id from InventoryEntity i where i.productId = :productId)")
    Long unitsExitedSince(@Param("productId") Integer productId, @Param("since") LocalDateTime since,
            @Param("status") MovementStatus status);
}

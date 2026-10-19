package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.InventoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryDAO extends JpaRepository<InventoryEntity, Integer> {

    Optional<InventoryEntity> findByStorageSpaceIdAndProductId(Integer storageSpaceId, Integer productId);

    @Query("select i from InventoryEntity i where i.productId = :productId and i.storageSpaceId in "
            + "(select s.id from StorageSpaceEntity s where s.warehouseId = :warehouseId and s.status = "
            + "com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus.ACTIVE)")
    List<InventoryEntity> findCandidates(@Param("warehouseId") Integer warehouseId,
            @Param("productId") Integer productId);

    @Query("select i from InventoryEntity i where (:productId is null or i.productId = :productId) "
            + "and (:spaceId is null or i.storageSpaceId = :spaceId) "
            + "and (:warehouseId is null or i.storageSpaceId in "
            + "(select s.id from StorageSpaceEntity s where s.warehouseId = :warehouseId))")
    Page<InventoryEntity> search(@Param("productId") Integer productId, @Param("warehouseId") Integer warehouseId,
            @Param("spaceId") Integer spaceId, Pageable pageable);

    @Query("select coalesce(sum(i.physicalQuantity), 0), coalesce(sum(i.reservedQuantity), 0) "
            + "from InventoryEntity i where i.productId = :productId")
    List<Object[]> totals(@Param("productId") Integer productId);
}

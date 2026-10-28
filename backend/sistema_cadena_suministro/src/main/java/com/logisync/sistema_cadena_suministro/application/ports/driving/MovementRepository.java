package com.logisync.sistema_cadena_suministro.application.ports.driving;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.queries.MovementFilter;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryMovement;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MovementRepository {
    InventoryMovement save(InventoryMovement movement);
    Optional<InventoryMovement> findById(Integer id);
    PageResult<InventoryMovement> search(MovementFilter filter, PageQuery pageQuery);
    long unitsExitedSince(Integer productId, LocalDateTime since);
}

package com.logisync.sistema_cadena_suministro.application.mappers;

import com.logisync.sistema_cadena_suministro.application.responses.InventoryLevelAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.MovementAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.MovementDetailAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.StockAllocationAppResponse;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryMovement;
import com.logisync.sistema_cadena_suministro.domain.inventory.MovementDetail;
import com.logisync.sistema_cadena_suministro.domain.inventory.StockAllocation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryApplicationMapper {

    MovementAppResponse toResponse(InventoryMovement movement);

    MovementDetailAppResponse toResponse(MovementDetail detail);

    StockAllocationAppResponse toResponse(StockAllocation allocation);

    default String typeCode(com.logisync.sistema_cadena_suministro.domain.inventory.MovementType type) {
        return type == null ? null : type.code();
    }

    default InventoryLevelAppResponse toResponse(InventoryItem item) {
        return new InventoryLevelAppResponse(item.getId(), item.getProductId(), item.getStorageSpaceId(),
                item.getWarehouseId(), item.getPhysicalQuantity(), item.getReservedQuantity(), item.available());
    }
}

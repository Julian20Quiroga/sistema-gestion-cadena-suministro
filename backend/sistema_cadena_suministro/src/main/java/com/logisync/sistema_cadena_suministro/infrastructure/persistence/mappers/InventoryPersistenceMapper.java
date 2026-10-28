package com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers;

import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryMovement;
import com.logisync.sistema_cadena_suministro.domain.inventory.MovementDetail;
import com.logisync.sistema_cadena_suministro.domain.inventory.MovementType;
import com.logisync.sistema_cadena_suministro.domain.inventory.MovementTypeFactory;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.InventoryEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.InventoryMovementEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.MovementDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryPersistenceMapper {

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    InventoryEntity toEntity(InventoryItem item);

    @Mapping(target = "warehouseId", ignore = true)
    InventoryItem toDomain(InventoryEntity entity);

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "movementType", source = "type")
    InventoryMovementEntity toEntity(InventoryMovement movement);

    @Mapping(target = "type", source = "movementType")
    InventoryMovement toDomain(InventoryMovementEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    MovementDetailEntity toEntity(MovementDetail detail);

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "storageSpaceId", ignore = true)
    MovementDetail toDomain(MovementDetailEntity entity);

    default String typeCode(MovementType type) {
        return type == null ? null : type.code();
    }

    default MovementType typeOf(String code) {
        return code == null ? null : MovementTypeFactory.of(code);
    }
}

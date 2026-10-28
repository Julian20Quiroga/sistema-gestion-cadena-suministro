package com.logisync.sistema_cadena_suministro.infrastructure.persistence.adapters;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driving.MovementRepository;
import com.logisync.sistema_cadena_suministro.application.queries.MovementFilter;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryMovement;
import com.logisync.sistema_cadena_suministro.domain.inventory.MovementDetail;
import com.logisync.sistema_cadena_suministro.domain.inventory.MovementStatus;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.InventoryDAO;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.InventoryMovementDAO;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.PageSupport;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.InventoryEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.InventoryMovementEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers.InventoryPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovementAdapterPersistence implements MovementRepository {

    private static final LocalDateTime MIN_DATE = LocalDateTime.of(1970, 1, 1, 0, 0);
    private static final LocalDateTime MAX_DATE = LocalDateTime.of(9999, 12, 31, 0, 0);

    private final InventoryMovementDAO movementDAO;
    private final InventoryDAO inventoryDAO;
    private final InventoryPersistenceMapper mapper;

    @Override
    public InventoryMovement save(InventoryMovement movement) {
        InventoryMovementEntity entity;
        if (movement.getId() == null) {
            entity = mapper.toEntity(movement);
        } else {
            entity = movementDAO.findById(movement.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Movement", movement.getId()));
            entity.setStatus(movement.getStatus());
        }
        return enrich(List.of(mapper.toDomain(movementDAO.save(entity)))).get(0);
    }

    @Override
    public Optional<InventoryMovement> findById(Integer id) {
        return movementDAO.findById(id).map(mapper::toDomain).map(m -> enrich(List.of(m)).get(0));
    }

    @Override
    public PageResult<InventoryMovement> search(MovementFilter filter, PageQuery pageQuery) {
        var page = movementDAO.search(filter.productId(), filter.type(),
                filter.from() == null ? MIN_DATE : filter.from(), filter.to() == null ? MAX_DATE : filter.to(),
                PageSupport.pageable(pageQuery, Sort.by(Sort.Direction.DESC, "movementDate")));
        List<InventoryMovement> enriched = enrich(page.getContent().stream().map(mapper::toDomain).toList());
        return new PageResult<>(enriched, page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @Override
    public long unitsExitedSince(Integer productId, LocalDateTime since) {
        return movementDAO.unitsExitedSince(productId, since, MovementStatus.APPLIED);
    }

    private List<InventoryMovement> enrich(List<InventoryMovement> movements) {
        List<Integer> inventoryIds = movements.stream().flatMap(m -> m.getDetails().stream())
                .map(MovementDetail::inventoryId).distinct().toList();
        Map<Integer, InventoryEntity> inventory = inventoryDAO.findAllById(inventoryIds).stream()
                .collect(Collectors.toMap(InventoryEntity::getId, Function.identity()));
        movements.forEach(m -> m.setDetails(m.getDetails().stream().map(d -> {
            InventoryEntity item = inventory.get(d.inventoryId());
            return new MovementDetail(d.inventoryId(), item == null ? null : item.getProductId(),
                    item == null ? null : item.getStorageSpaceId(), d.quantity(), d.reservedDelta());
        }).toList()));
        return movements;
    }
}

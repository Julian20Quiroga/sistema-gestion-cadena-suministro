package com.logisync.sistema_cadena_suministro.infrastructure.persistence.adapters;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driving.WarehouseRepository;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.models.Warehouse;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.PageSupport;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.WarehouseDAO;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.WarehouseEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers.WarehousePersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WarehouseAdapterPersistence implements WarehouseRepository {

    private final WarehouseDAO warehouseDAO;
    private final WarehousePersistenceMapper mapper;

    @Override
    public Warehouse create(Warehouse warehouse) {
        WarehouseEntity entity = mapper.toEntity(warehouse);
        entity.setStatus(SoftDeleteStatus.ACTIVE);
        return mapper.toDomain(warehouseDAO.save(entity));
    }

    @Override
    public Warehouse update(Warehouse warehouse, Integer id) {
        WarehouseEntity entity = warehouseDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", id));
        mapper.updateEntity(warehouse, entity);
        return mapper.toDomain(warehouseDAO.save(entity));
    }

    @Override
    public void delete(Integer id) {
        WarehouseEntity entity = warehouseDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", id));
        entity.setStatus(SoftDeleteStatus.DELETED);
        warehouseDAO.save(entity);
    }

    @Override
    public Optional<Warehouse> findById(Integer id) {
        return warehouseDAO.findById(id).map(mapper::toDomain);
    }

    @Override
    public PageResult<Warehouse> findAll(String name, SoftDeleteStatus status, PageQuery pageQuery) {
        String pattern = "%" + (name == null ? "" : name.trim().toLowerCase(Locale.ROOT)) + "%";
        return PageSupport.toResult(warehouseDAO.search(pattern, status, PageSupport.pageable(pageQuery)),
                mapper::toDomain);
    }
}

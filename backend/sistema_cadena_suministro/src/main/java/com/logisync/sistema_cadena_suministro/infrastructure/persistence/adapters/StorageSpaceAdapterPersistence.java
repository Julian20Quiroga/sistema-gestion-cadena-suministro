package com.logisync.sistema_cadena_suministro.infrastructure.persistence.adapters;

import com.logisync.sistema_cadena_suministro.application.ports.driving.StorageSpaceRepository;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.models.StorageSpace;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.StorageSpaceDAO;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.StorageSpaceEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers.StorageSpacePersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StorageSpaceAdapterPersistence implements StorageSpaceRepository {

    private final StorageSpaceDAO storageSpaceDAO;
    private final StorageSpacePersistenceMapper mapper;

    @Override
    public StorageSpace create(StorageSpace space) {
        return mapper.toDomain(storageSpaceDAO.save(mapper.toEntity(space)));
    }

    @Override
    public StorageSpace update(StorageSpace space) {
        StorageSpaceEntity entity = storageSpaceDAO.findById(space.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Storage space", space.getId()));
        entity.setName(space.getName());
        entity.setCapacity(space.getCapacity());
        return mapper.toDomain(storageSpaceDAO.save(entity));
    }

    @Override
    public void delete(Integer id) {
        StorageSpaceEntity entity = storageSpaceDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Storage space", id));
        entity.setStatus(SoftDeleteStatus.DELETED);
        storageSpaceDAO.save(entity);
    }

    @Override
    public Optional<StorageSpace> findById(Integer id) {
        return storageSpaceDAO.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<StorageSpace> findActiveByWarehouse(Integer warehouseId) {
        return storageSpaceDAO.findByWarehouseIdAndStatus(warehouseId, SoftDeleteStatus.ACTIVE).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public boolean hasActiveChildren(Integer id) {
        return storageSpaceDAO.existsByParentIdAndStatus(id, SoftDeleteStatus.ACTIVE);
    }
}

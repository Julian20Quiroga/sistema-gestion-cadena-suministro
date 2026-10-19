package com.logisync.sistema_cadena_suministro.infrastructure.persistence.adapters;

import com.logisync.sistema_cadena_suministro.application.ports.driving.LayoutSnapshotRepository;
import com.logisync.sistema_cadena_suministro.domain.models.LayoutSnapshot;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.LayoutSnapshotDAO;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.LayoutSnapshotEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers.LayoutSnapshotPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional
public class LayoutSnapshotAdapterPersistence implements LayoutSnapshotRepository {

    private final LayoutSnapshotDAO dao;
    private final LayoutSnapshotPersistenceMapper mapper;

    @Override
    public LayoutSnapshot save(LayoutSnapshot snapshot) {
        LayoutSnapshotEntity entity = mapper.toEntity(snapshot);
        LayoutSnapshotEntity saved = dao.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<LayoutSnapshot> findById(Integer id) {
        return dao.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<LayoutSnapshot> findByWarehouseId(Integer warehouseId) {
        return dao.findByWarehouseIdOrderByCreatedDateDesc(warehouseId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}

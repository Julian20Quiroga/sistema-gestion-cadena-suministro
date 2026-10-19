package com.logisync.sistema_cadena_suministro.infrastructure.persistence.adapters;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driving.DispatchRepository;
import com.logisync.sistema_cadena_suministro.domain.enums.DispatchStatus;
import com.logisync.sistema_cadena_suministro.domain.models.DetailDispatch;
import com.logisync.sistema_cadena_suministro.domain.models.Dispatch;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.DispatchDAO;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.PageSupport;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.DetailDispatchEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.DispatchEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers.DispatchPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional
public class DispatchAdapterPersistence implements DispatchRepository {

    private final DispatchDAO dao;
    private final DispatchPersistenceMapper mapper;

    @Override
    public Dispatch save(Dispatch dispatch) {
        DispatchEntity entity;
        if (dispatch.getId() != null) {
            entity = dao.findById(dispatch.getId()).orElseGet(() -> mapper.toEntity(dispatch));
            entity.setStatus(dispatch.getStatus());
            entity.setWarehouseId(dispatch.getWarehouseId());
            entity.setVehicleId(dispatch.getVehicleId());
            entity.setOrderId(dispatch.getOrderId());

            entity.getDetails().clear();
            for (DetailDispatch d : dispatch.getDetails()) {
                DetailDispatchEntity de = new DetailDispatchEntity(
                        entity,
                        d.getDetailOrderId(),
                        d.getStorageSpaceId(),
                        d.getInventoryId(),
                        d.getQuantity()
                );
                de.setId(d.getId());
                entity.getDetails().add(de);
            }
        } else {
            entity = mapper.toEntity(dispatch);
            entity.setDetails(new ArrayList<>());
            for (DetailDispatch d : dispatch.getDetails()) {
                DetailDispatchEntity de = new DetailDispatchEntity(
                        entity,
                        d.getDetailOrderId(),
                        d.getStorageSpaceId(),
                        d.getInventoryId(),
                        d.getQuantity()
                );
                entity.getDetails().add(de);
            }
        }
        DispatchEntity saved = dao.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Dispatch> findById(Integer id) {
        return dao.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByOrderId(Integer orderId) {
        return dao.existsByOrderId(orderId);
    }

    @Override
    public PageResult<Dispatch> findAll(Integer warehouseId, Integer orderId,
                                        DispatchStatus status, PageQuery pageQuery) {
        Page<DispatchEntity> page = dao.search(warehouseId, orderId, status, PageSupport.pageable(pageQuery));
        return PageSupport.toResult(page, mapper::toDomain);
    }
}

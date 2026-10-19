package com.logisync.sistema_cadena_suministro.infrastructure.persistence.adapters;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driving.SupplyNeedRepository;
import com.logisync.sistema_cadena_suministro.domain.enums.SupplyNeedStatus;
import com.logisync.sistema_cadena_suministro.domain.models.SupplyNeed;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.PageSupport;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.SupplyNeedDAO;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.SupplyNeedEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers.PurchaseOrderPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SupplyNeedAdapterPersistence implements SupplyNeedRepository {

    private final SupplyNeedDAO dao;
    private final PurchaseOrderPersistenceMapper mapper;

    @Override
    public SupplyNeed save(SupplyNeed supplyNeed) {
        SupplyNeedEntity entity = mapper.toEntity(supplyNeed);
        SupplyNeedEntity saved = dao.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<SupplyNeed> findById(Integer id) {
        return dao.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsOpenByProduct(Integer productId) {
        return dao.existsByProductIdAndStatus(productId, SupplyNeedStatus.OPEN);
    }

    @Override
    public Optional<SupplyNeed> findOpenByProduct(Integer productId) {
        return dao.findFirstByProductIdAndStatusOrderByIdDesc(productId, SupplyNeedStatus.OPEN)
                .map(mapper::toDomain);
    }

    @Override
    public PageResult<SupplyNeed> findAll(SupplyNeedStatus status, PageQuery pageQuery) {
        Page<SupplyNeedEntity> page = status == null
                ? dao.findAll(PageSupport.pageable(pageQuery))
                : dao.findByStatusOrderByIdDesc(status, PageSupport.pageable(pageQuery));
        return PageSupport.toResult(page, mapper::toDomain);
    }
}

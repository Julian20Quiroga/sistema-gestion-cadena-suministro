package com.logisync.sistema_cadena_suministro.infrastructure.persistence.adapters;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driving.PurchaseOrderRepository;
import com.logisync.sistema_cadena_suministro.domain.enums.PurchaseOrderStatus;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrder;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrderDetail;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.PageSupport;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.PurchaseOrderDAO;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.PurchaseOrderDetailEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.PurchaseOrderEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers.PurchaseOrderPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PurchaseOrderAdapterPersistence implements PurchaseOrderRepository {

    private final PurchaseOrderDAO dao;
    private final PurchaseOrderPersistenceMapper mapper;

    @Override
    public PurchaseOrder save(PurchaseOrder order) {
        PurchaseOrderEntity entity;
        if (order.getId() != null) {
            entity = dao.findById(order.getId()).orElseGet(() -> mapper.toEntity(order));
            entity.setStatus(order.getStatus());
            entity.setSupplierId(order.getSupplierId());
            entity.setWarehouseId(order.getWarehouseId());
            entity.setOrderDate(order.getOrderDate());

            // Sync details
            entity.getDetails().clear();
            for (PurchaseOrderDetail d : order.getDetails()) {
                PurchaseOrderDetailEntity de = new PurchaseOrderDetailEntity(
                        d.getProductId(),
                        entity,
                        d.getQuantity(),
                        d.getReceivedQuantity(),
                        d.getUnitPrice()
                );
                de.setId(d.getId());
                entity.getDetails().add(de);
            }
        } else {
            entity = mapper.toEntity(order);
            entity.setDetails(new ArrayList<>());
            for (PurchaseOrderDetail d : order.getDetails()) {
                PurchaseOrderDetailEntity de = new PurchaseOrderDetailEntity(
                        d.getProductId(),
                        entity,
                        d.getQuantity(),
                        d.getReceivedQuantity(),
                        d.getUnitPrice()
                );
                entity.getDetails().add(de);
            }
        }
        PurchaseOrderEntity saved = dao.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<PurchaseOrder> findById(Integer id) {
        return dao.findById(id).map(mapper::toDomain);
    }

    @Override
    public PageResult<PurchaseOrder> findAll(Integer supplierId, Integer warehouseId,
                                            PurchaseOrderStatus status, PageQuery pageQuery) {
        Page<PurchaseOrderEntity> page = dao.search(supplierId, warehouseId, status, PageSupport.pageable(pageQuery));
        return PageSupport.toResult(page, mapper::toDomain);
    }
}

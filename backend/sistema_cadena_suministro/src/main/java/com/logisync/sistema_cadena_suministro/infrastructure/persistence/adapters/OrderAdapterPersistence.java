package com.logisync.sistema_cadena_suministro.infrastructure.persistence.adapters;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driving.OrderRepository;
import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;
import com.logisync.sistema_cadena_suministro.domain.models.Order;
import com.logisync.sistema_cadena_suministro.domain.models.OrderDetail;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.OrderDAO;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.PageSupport;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.OrderDetailEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.OrderEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers.OrderPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional
public class OrderAdapterPersistence implements OrderRepository {

    private final OrderDAO dao;
    private final OrderPersistenceMapper mapper;

    @Override
    public Order save(Order order) {
        OrderEntity entity;
        if (order.getId() != null) {
            entity = dao.findById(order.getId()).orElseGet(() -> mapper.toEntity(order));
            entity.setStatus(order.getStatus());
            entity.setCustomerName(order.getCustomerName());
            entity.setCustomerPhone(order.getCustomerPhone());
            entity.setDeliveryAddress(order.getDeliveryAddress());
            entity.setDeliveryCity(order.getDeliveryCity());
            entity.setDeliveryCountry(order.getDeliveryCountry());
            entity.setDeliveryLatitude(order.getDeliveryLatitude());
            entity.setDeliveryLongitude(order.getDeliveryLongitude());

            entity.getDetails().clear();
            for (OrderDetail d : order.getDetails()) {
                OrderDetailEntity de = new OrderDetailEntity(d.getProductId(), entity, d.getQuantity(), d.getUnitPrice());
                de.setId(d.getId());
                entity.getDetails().add(de);
            }
        } else {
            entity = mapper.toEntity(order);
            entity.setDetails(new ArrayList<>());
            for (OrderDetail d : order.getDetails()) {
                OrderDetailEntity de = new OrderDetailEntity(d.getProductId(), entity, d.getQuantity(), d.getUnitPrice());
                entity.getDetails().add(de);
            }
        }
        OrderEntity saved = dao.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Order> findById(Integer id) {
        return dao.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Order> findByOrderReference(String orderReference) {
        return dao.findByOrderReference(orderReference).map(mapper::toDomain);
    }

    @Override
    public boolean existsByOrderReference(String orderReference) {
        return dao.existsByOrderReference(orderReference);
    }

    @Override
    public PageResult<Order> findAll(String reference, OrderStatus status, PageQuery pageQuery) {
        Page<OrderEntity> page = dao.search(reference, status, PageSupport.pageable(pageQuery));
        return PageSupport.toResult(page, mapper::toDomain);
    }
}

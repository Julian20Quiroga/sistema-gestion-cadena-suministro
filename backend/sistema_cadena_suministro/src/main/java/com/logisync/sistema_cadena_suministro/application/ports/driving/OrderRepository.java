package com.logisync.sistema_cadena_suministro.application.ports.driving;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;
import com.logisync.sistema_cadena_suministro.domain.models.Order;
import com.logisync.sistema_cadena_suministro.domain.models.DeliveredDemandRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Integer id);
    Optional<Order> findByOrderReference(String orderReference);
    boolean existsByOrderReference(String orderReference);
    PageResult<Order> findAll(String reference, OrderStatus status, PageQuery pageQuery);
    List<DeliveredDemandRecord> findDeliveredDemand(LocalDateTime fromDate, LocalDateTime toDate, Integer productId);
}

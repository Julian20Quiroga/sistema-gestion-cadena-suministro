package com.logisync.sistema_cadena_suministro.application.service;

import com.logisync.sistema_cadena_suministro.application.commands.CreateOrderCommand;
import com.logisync.sistema_cadena_suministro.application.mappers.OrderApplicationMapper;
import com.logisync.sistema_cadena_suministro.application.mediator.OrderColleague;
import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreateOrderUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryOrderUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.UpdateOrderStatusUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.InventoryRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.OrderRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.ProductRepository;
import com.logisync.sistema_cadena_suministro.application.responses.OrderAppResponse;
import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ConflictException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.models.Order;
import com.logisync.sistema_cadena_suministro.domain.models.OrderDetail;
import com.logisync.sistema_cadena_suministro.domain.models.Product;
import com.logisync.sistema_cadena_suministro.domain.orders.chain.OrderCreationContext;
import com.logisync.sistema_cadena_suministro.domain.orders.chain.OrderCreationValidator;
import com.logisync.sistema_cadena_suministro.domain.orders.chain.OrderCustomerDataValidator;
import com.logisync.sistema_cadena_suministro.domain.orders.chain.OrderProductsActiveValidator;
import com.logisync.sistema_cadena_suministro.domain.orders.chain.OrderStockAvailabilityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService implements CreateOrderUseCase, UpdateOrderStatusUseCase, QueryOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderColleague orderColleague;
    private final OrderApplicationMapper mapper;

    @Override
    @Transactional
    public OrderAppResponse create(CreateOrderCommand command) {
        if (orderRepository.existsByOrderReference(command.orderReference())) {
            throw new ConflictException("Order with reference " + command.orderReference() + " already exists");
        }

        Order order = new Order();
        order.setOrderReference(command.orderReference());
        order.setDeliveryAddress(command.deliveryAddress());
        order.setDeliveryCity(command.deliveryCity());
        order.setDeliveryCountry(command.deliveryCountry());
        order.setCustomerPhone(command.customerPhone());
        order.setCustomerName(command.customerName());
        order.setDeliveryLatitude(command.deliveryLatitude());
        order.setDeliveryLongitude(command.deliveryLongitude());

        List<OrderDetail> details = new ArrayList<>();
        Map<Integer, Product> products = new HashMap<>();
        Map<Integer, Long> availableStock = new HashMap<>();

        if (command.items() != null) {
            for (var item : command.items()) {
                details.add(new OrderDetail(null, item.productId(), null, item.quantity(), item.unitPrice()));
                productRepository.findById(item.productId()).ifPresent(p -> {
                    products.put(p.getId(), p);
                    long available = inventoryRepository.totalsByProduct(p.getId()).available();
                    availableStock.put(p.getId(), available);
                });
            }
        }
        order.setDetails(details);

        OrderCreationValidator validator = new OrderCustomerDataValidator();
        validator.linkWith(new OrderProductsActiveValidator())
                 .linkWith(new OrderStockAvailabilityValidator());
        validator.validate(new OrderCreationContext(order, products, availableStock));

        Order saved = orderRepository.save(order);
        orderColleague.onOrderCreated(saved);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public OrderAppResponse startPreparation(Integer orderId) {
        Order order = find(orderId);
        order.startPreparation();
        return mapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderAppResponse markDispatched(Integer orderId) {
        Order order = find(orderId);
        order.markDispatched();
        return mapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderAppResponse markDelivered(Integer orderId) {
        Order order = find(orderId);
        order.markDelivered();
        return mapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderAppResponse cancel(Integer orderId) {
        Order order = find(orderId);
        order.cancel();
        Order saved = orderRepository.save(order);
        orderColleague.onOrderCancelled(saved);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderAppResponse findById(Integer id) {
        return mapper.toResponse(find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderAppResponse findByReference(String reference) {
        return orderRepository.findByOrderReference(reference)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Order with reference", reference));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<OrderAppResponse> findAll(String reference, OrderStatus status, PageQuery pageQuery) {
        return orderRepository.findAll(reference, status, pageQuery).map(mapper::toResponse);
    }

    private Order find(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    }
}

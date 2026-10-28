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

/**
 * Servicio de aplicación para la gestión de pedidos de clientes (ventas).
 * Implementa los patrones:
 * - Chain of Responsibility: Validación secuencial de datos de cliente, estado de productos y stock.
 * - State: Control de transiciones de estado de la orden (REGISTRADO -> PREPARACION -> EN_RUTA -> ENTREGADO / CANCELADO).
 * - Mediator: Emisión de eventos desacoplados hacia otros módulos a través de OrderColleague.
 */
@Service
@RequiredArgsConstructor
public class OrderService implements CreateOrderUseCase, UpdateOrderStatusUseCase, QueryOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderColleague orderColleague;
    private final OrderApplicationMapper mapper;

    /**
     * Registra un nuevo pedido de venta validando reglas comerciales mediante la cadena de responsabilidad.
     * Patrones aplicados:
     * - Chain of Responsibility: Ejecuta OrderCustomerDataValidator -> OrderProductsActiveValidator -> OrderStockAvailabilityValidator.
     * - Mediator: Notifica al mediador la creación de la orden mediante orderColleague.
     */
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

        // Patrón Chain of Responsibility
        OrderCreationValidator validator = new OrderCustomerDataValidator();
        validator.linkWith(new OrderProductsActiveValidator())
                 .linkWith(new OrderStockAvailabilityValidator());
        validator.validate(new OrderCreationContext(order, products, availableStock));

        Order saved = orderRepository.save(order);
        // Patrón Mediator
        orderColleague.onOrderCreated(saved);
        return mapper.toResponse(saved);
    }

    /**
     * Cambia el estado del pedido a preparación en almacén.
     * Patrón aplicado: State (transición REGISTRADO -> PREPARACION).
     */
    @Override
    @Transactional
    public OrderAppResponse startPreparation(Integer orderId) {
        Order order = find(orderId);
        order.startPreparation();
        return mapper.toResponse(orderRepository.save(order));
    }

    /**
     * Marca el pedido como despachado y en tránsito vehicular.
     * Patrón aplicado: State (transición PREPARACION -> EN_RUTA).
     */
    @Override
    @Transactional
    public OrderAppResponse markDispatched(Integer orderId) {
        Order order = find(orderId);
        order.markDispatched();
        return mapper.toResponse(orderRepository.save(order));
    }

    /**
     * Confirma la entrega efectiva del pedido al cliente.
     * Patrón aplicado: State (transición EN_RUTA -> ENTREGADO).
     */
    @Override
    @Transactional
    public OrderAppResponse markDelivered(Integer orderId) {
        Order order = find(orderId);
        order.markDelivered();
        return mapper.toResponse(orderRepository.save(order));
    }

    /**
     * Cancela el pedido y notifica al mediador para coordinar compensaciones.
     * Patrones aplicados: State (transición a CANCELADO) y Mediator (notifica onOrderCancelled).
     */
    @Override
    @Transactional
    public OrderAppResponse cancel(Integer orderId) {
        Order order = find(orderId);
        order.cancel();
        Order saved = orderRepository.save(order);
        orderColleague.onOrderCancelled(saved);
        return mapper.toResponse(saved);
    }

    /**
     * Consulta una orden por su identificador primario.
     */
    @Override
    @Transactional(readOnly = true)
    public OrderAppResponse findById(Integer id) {
        return mapper.toResponse(find(id));
    }

    /**
     * Consulta una orden por su código de referencia alfanumérico.
     */
    @Override
    @Transactional(readOnly = true)
    public OrderAppResponse findByReference(String reference) {
        return orderRepository.findByOrderReference(reference)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Order with reference", reference));
    }

    /**
     * Lista órdenes de venta con paginación y filtros opcionales por referencia y estado.
     */
    @Override
    @Transactional(readOnly = true)
    public PageResult<OrderAppResponse> findAll(String reference, OrderStatus status, PageQuery pageQuery) {
        return orderRepository.findAll(reference, status, pageQuery).map(mapper::toResponse);
    }

    /**
     * Busca la entidad de orden por su identificador o arroja excepción si no existe.
     */
    private Order find(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    }
}

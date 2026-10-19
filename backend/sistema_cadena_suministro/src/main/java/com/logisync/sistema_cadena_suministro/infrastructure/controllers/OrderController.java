package com.logisync.sistema_cadena_suministro.infrastructure.controllers;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreateOrderUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryOrderUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.UpdateOrderStatusUseCase;
import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreateOrderRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.OrderResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.PageResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers.OrderRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Customer Orders", description = "Customer order life cycle, validation and tracking")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final QueryOrderUseCase queryOrderUseCase;
    private final OrderRestMapper mapper;

    /**
     * Crea un pedido de cliente ejecutando validaciones de cadena y cálculo de precios con recargos.
     * Patrones aplicados: Chain of Responsibility (validación de reglas) y Decorator (cálculo de recargos comerciales).
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('PRESIDENT', 'BRANCH_ADMIN')")
    @Operation(summary = "Create customer order")
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid CreateOrderRequest request) {
        var res = createOrderUseCase.create(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(res));
    }

    /**
     * Transiciona el pedido al estado 'IN_PREPARATION' validando la máquina de estados.
     * Patrón aplicado: State (OrderState delega la transición permitida).
     */
    @PostMapping("/{id}/start-preparation")
    @PreAuthorize("hasAnyRole('PRESIDENT', 'BRANCH_ADMIN')")
    @Operation(summary = "Start order preparation")
    public ResponseEntity<OrderResponse> startPreparation(@PathVariable("id") Integer id) {
        var res = updateOrderStatusUseCase.startPreparation(id);
        return ResponseEntity.ok(mapper.toResponse(res));
    }

    /**
     * Transiciona el estado del pedido a 'DISPATCHED' una vez despachado en transporte.
     * Patrón aplicado: State (OrderState gestiona el ciclo de vida del pedido).
     */
    @PostMapping("/{id}/mark-dispatched")
    @PreAuthorize("hasAnyRole('PRESIDENT', 'BRANCH_ADMIN')")
    @Operation(summary = "Mark order as dispatched")
    public ResponseEntity<OrderResponse> markDispatched(@PathVariable("id") Integer id) {
        var res = updateOrderStatusUseCase.markDispatched(id);
        return ResponseEntity.ok(mapper.toResponse(res));
    }

    /**
     * Marca el pedido como entregado a satisfacción del cliente final ('DELIVERED').
     * Patrón aplicado: State (OrderState confirma el estado terminal exitoso).
     */
    @PostMapping("/{id}/mark-delivered")
    @PreAuthorize("hasAnyRole('PRESIDENT', 'BRANCH_ADMIN')")
    @Operation(summary = "Mark order as delivered")
    public ResponseEntity<OrderResponse> markDelivered(@PathVariable("id") Integer id) {
        var res = updateOrderStatusUseCase.markDelivered(id);
        return ResponseEntity.ok(mapper.toResponse(res));
    }

    /**
     * Cancela el pedido y libera reservas o necesidades pendientes si las reglas lo permiten.
     * Patrón aplicado: State (OrderState valida si la cancelación es admisible en el estado actual).
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('PRESIDENT', 'BRANCH_ADMIN')")
    @Operation(summary = "Cancel customer order")
    public ResponseEntity<OrderResponse> cancel(@PathVariable("id") Integer id) {
        var res = updateOrderStatusUseCase.cancel(id);
        return ResponseEntity.ok(mapper.toResponse(res));
    }

    /**
     * Consulta el detalle integral de un pedido por su identificador primario.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRESIDENT', 'BRANCH_ADMIN')")
    @Operation(summary = "Find order by ID")
    public ResponseEntity<OrderResponse> findById(@PathVariable("id") Integer id) {
        var res = queryOrderUseCase.findById(id);
        return ResponseEntity.ok(mapper.toResponse(res));
    }

    /**
     * Localiza un pedido por su código de referencia alfanumérico único.
     */
    @GetMapping("/by-reference/{reference}")
    @PreAuthorize("hasAnyRole('PRESIDENT', 'BRANCH_ADMIN')")
    @Operation(summary = "Find order by unique reference")
    public ResponseEntity<OrderResponse> findByReference(@PathVariable("reference") String reference) {
        var res = queryOrderUseCase.findByReference(reference);
        return ResponseEntity.ok(mapper.toResponse(res));
    }

    /**
     * Consulta paginada y filtrada de pedidos según referencia y estado del ciclo de vida.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('PRESIDENT', 'BRANCH_ADMIN')")
    @Operation(summary = "Search orders with pagination")
    public ResponseEntity<PageResponse<OrderResponse>> findAll(
            @RequestParam(name = "reference", required = false) String reference,
            @RequestParam(name = "status", required = false) OrderStatus status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(PageResponse.from(
                queryOrderUseCase.findAll(reference, status, PageQuery.of(page, size)),
                mapper::toResponse));
    }
}

package com.logisync.sistema_cadena_suministro.application.facade;

import com.logisync.sistema_cadena_suministro.application.commands.ConsumeReservationCommand;
import com.logisync.sistema_cadena_suministro.application.commands.CreateDispatchCommand;
import com.logisync.sistema_cadena_suministro.application.mappers.DispatchApplicationMapper;
import com.logisync.sistema_cadena_suministro.application.ports.driven.ConsumeReservedStockUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.ReleaseStockUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.ReserveStockUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.UpdateOrderStatusUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.DispatchRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.OrderRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.VehicleRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.WarehouseRepository;
import com.logisync.sistema_cadena_suministro.application.responses.DispatchAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.StockAllocationAppResponse;
import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ConflictException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.models.DetailDispatch;
import com.logisync.sistema_cadena_suministro.domain.models.Dispatch;
import com.logisync.sistema_cadena_suministro.domain.models.Order;
import com.logisync.sistema_cadena_suministro.domain.models.OrderDetail;
import com.logisync.sistema_cadena_suministro.domain.models.Vehicle;
import com.logisync.sistema_cadena_suministro.domain.models.Warehouse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Orquestador principal de despachos y salidas de mercancía.
 * Implementa los patrones:
 * - Facade: Provee una interfaz unificada y simplificada que encapsula las operaciones complejas de
 *   reserva de inventario, actualización de estados de pedidos, validación de flota y emisión de despachos.
 * - State: Gestiona la máquina de estados del despacho (PENDING -> PACKED -> IN_TRANSIT -> DELIVERED).
 */
@Component
@RequiredArgsConstructor
public class DefaultDispatchFacade implements DispatchFacade {

    private final DispatchRepository dispatchRepository;
    private final OrderRepository orderRepository;
    private final WarehouseRepository warehouseRepository;
    private final VehicleRepository vehicleRepository;
    private final ReserveStockUseCase reserveStockUseCase;
    private final ReleaseStockUseCase releaseStockUseCase;
    private final ConsumeReservedStockUseCase consumeReservedStockUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final DispatchApplicationMapper mapper;

    /**
     * Orquesta la creación del despacho vinculando pedido, almacén, vehículo y reservando stock en bodega.
     * Patrón aplicado: Facade (coordina subsistemas de órdenes, inventario y vehículos en una sola transacción).
     */
    @Override
    @Transactional
    public DispatchAppResponse createDispatchWithStockReservation(CreateDispatchCommand command) {
        if (dispatchRepository.existsByOrderId(command.orderId())) {
            throw new ConflictException("Dispatch already exists for order " + command.orderId());
        }

        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", command.orderId()));

        if (order.getStatus() != OrderStatus.REGISTRADO && order.getStatus() != OrderStatus.PREPARACION) {
            throw new BusinessRuleException("Cannot dispatch order in state " + order.getStatus());
        }

        Warehouse warehouse = warehouseRepository.findById(command.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", command.warehouseId()));
        if (!warehouse.isActive()) {
            throw new BusinessRuleException("Warehouse " + warehouse.getId() + " is inactive");
        }

        if (command.vehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(command.vehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle", command.vehicleId()));
            if (!vehicle.isActive()) {
                throw new BusinessRuleException("Vehicle " + vehicle.getId() + " is inactive");
            }
        }

        Dispatch dispatch = new Dispatch();
        dispatch.setOrderId(order.getId());
        dispatch.setWarehouseId(warehouse.getId());
        dispatch.setVehicleId(command.vehicleId());

        List<DetailDispatch> details = new ArrayList<>();
        for (OrderDetail line : order.getDetails()) {
            StockAllocationAppResponse allocation = reserveStockUseCase.reserve(warehouse.getId(), line.getProductId(), line.getQuantity());
            details.add(new DetailDispatch(null, null, line.getId(), allocation.storageSpaceId(), allocation.inventoryId(), line.getQuantity()));
        }
        dispatch.setDetails(details);

        Dispatch saved = dispatchRepository.save(dispatch);
        updateOrderStatusUseCase.startPreparation(order.getId());
        return mapper.toResponse(saved);
    }

    /**
     * Inicia la fase de empaque del despacho en almacén.
     * Patrón aplicado: State (transición de estado de PENDING a PACKED).
     */
    @Override
    @Transactional
    public DispatchAppResponse startPreparation(Integer dispatchId) {
        Dispatch dispatch = find(dispatchId);
        dispatch.startPreparation();
        return mapper.toResponse(dispatchRepository.save(dispatch));
    }

    /**
     * Marca el despacho como enviado, consumiendo definitivamente el stock previamente reservado.
     * Patrones aplicados: Facade (coordina consumo de inventario y estado del pedido) y State (transición a IN_TRANSIT).
     */
    @Override
    @Transactional
    public DispatchAppResponse shipAndConsumeStock(Integer dispatchId) {
        Dispatch dispatch = find(dispatchId);
        dispatch.startTransport();

        // Consumir el stock reservado para cada línea del despacho
        for (DetailDispatch d : dispatch.getDetails()) {
            consumeReservedStockUseCase.consume(new ConsumeReservationCommand(
                    d.getInventoryId(),
                    d.getQuantity(),
                    "DISPATCH-" + dispatch.getId()
            ));
        }

        Dispatch saved = dispatchRepository.save(dispatch);
        updateOrderStatusUseCase.markDispatched(dispatch.getOrderId());
        return mapper.toResponse(saved);
    }

    /**
     * Confirma la entrega exitosa de la mercancía en el destino final.
     * Patrones aplicados: State (transición a DELIVERED) y Facade (sincroniza el pedido asociado).
     */
    @Override
    @Transactional
    public DispatchAppResponse markDelivered(Integer dispatchId) {
        Dispatch dispatch = find(dispatchId);
        dispatch.markDelivered();
        Dispatch saved = dispatchRepository.save(dispatch);
        updateOrderStatusUseCase.markDelivered(dispatch.getOrderId());
        return mapper.toResponse(saved);
    }

    /**
     * Cancela el despacho y libera las reservas de inventario regresándolas al stock disponible.
     * Patrones aplicados: Facade (compensa reservas de bodega y cancela el pedido) y State (transición a FAILED).
     */
    @Override
    @Transactional
    public DispatchAppResponse cancelAndReleaseStock(Integer dispatchId) {
        Dispatch dispatch = find(dispatchId);
        dispatch.cancel();

        for (DetailDispatch d : dispatch.getDetails()) {
            releaseStockUseCase.release(d.getInventoryId(), d.getQuantity());
        }

        Dispatch saved = dispatchRepository.save(dispatch);
        updateOrderStatusUseCase.cancel(dispatch.getOrderId());
        return mapper.toResponse(saved);
    }

    /**
     * Busca la entidad de despacho por su identificador primario o lanza excepción si no existe.
     */
    private Dispatch find(Integer dispatchId) {
        return dispatchRepository.findById(dispatchId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispatch", dispatchId));
    }
}

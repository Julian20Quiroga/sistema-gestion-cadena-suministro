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
import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ConflictException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.application.responses.StockAllocationAppResponse;
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
 * Facade: Concrete orchestrator unifying dispatch creation, inventory reservation, and shipment.
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
            // Reserve stock using best-fit single location in the selected warehouse
            StockAllocationAppResponse allocation = reserveStockUseCase.reserve(warehouse.getId(), line.getProductId(), line.getQuantity());
            details.add(new DetailDispatch(null, null, line.getId(), allocation.storageSpaceId(), allocation.inventoryId(), line.getQuantity()));
        }
        dispatch.setDetails(details);

        Dispatch saved = dispatchRepository.save(dispatch);
        updateOrderStatusUseCase.startPreparation(order.getId());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public DispatchAppResponse startPreparation(Integer dispatchId) {
        Dispatch dispatch = find(dispatchId);
        dispatch.startPreparation();
        return mapper.toResponse(dispatchRepository.save(dispatch));
    }

    @Override
    @Transactional
    public DispatchAppResponse shipAndConsumeStock(Integer dispatchId) {
        Dispatch dispatch = find(dispatchId);
        dispatch.startTransport();

        // Consume reserved stock for each dispatch line
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

    @Override
    @Transactional
    public DispatchAppResponse markDelivered(Integer dispatchId) {
        Dispatch dispatch = find(dispatchId);
        dispatch.markDelivered();
        Dispatch saved = dispatchRepository.save(dispatch);
        updateOrderStatusUseCase.markDelivered(dispatch.getOrderId());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public DispatchAppResponse cancelAndReleaseStock(Integer dispatchId) {
        Dispatch dispatch = find(dispatchId);
        dispatch.cancel();

        // Release reserved stock back to available pool
        for (DetailDispatch d : dispatch.getDetails()) {
            releaseStockUseCase.release(d.getInventoryId(), d.getQuantity());
        }

        Dispatch saved = dispatchRepository.save(dispatch);
        updateOrderStatusUseCase.cancel(dispatch.getOrderId());
        return mapper.toResponse(saved);
    }

    private Dispatch find(Integer dispatchId) {
        return dispatchRepository.findById(dispatchId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispatch", dispatchId));
    }
}

package com.logisync.sistema_cadena_suministro.application.service;

import com.logisync.sistema_cadena_suministro.application.commands.CreateVehicleCommand;
import com.logisync.sistema_cadena_suministro.application.commands.OptimizeRouteCommand;
import com.logisync.sistema_cadena_suministro.application.mappers.VehicleApplicationMapper;
import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreateVehicleUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.OptimizeRouteUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryVehicleUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.RotateVehicleApiKeyUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.OrderRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.ProductRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.VehicleRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.WarehouseRepository;
import com.logisync.sistema_cadena_suministro.application.responses.OptimizedRouteAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.VehicleAppResponse;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ConflictException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.models.Order;
import com.logisync.sistema_cadena_suministro.domain.models.OrderDetail;
import com.logisync.sistema_cadena_suministro.domain.models.Product;
import com.logisync.sistema_cadena_suministro.domain.models.Vehicle;
import com.logisync.sistema_cadena_suministro.domain.models.Warehouse;
import com.logisync.sistema_cadena_suministro.domain.routes.GeoPoint;
import com.logisync.sistema_cadena_suministro.domain.routes.OptimizedRoute;
import com.logisync.sistema_cadena_suministro.domain.routes.RouteOptimizationContext;
import com.logisync.sistema_cadena_suministro.domain.routes.strategy.RouteOptimizationStrategy;
import com.logisync.sistema_cadena_suministro.domain.routes.strategy.RouteStrategyResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService implements CreateVehicleUseCase, RotateVehicleApiKeyUseCase,
        QueryVehicleUseCase, OptimizeRouteUseCase {

    private final VehicleRepository vehicleRepository;
    private final WarehouseRepository warehouseRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final RouteStrategyResolver strategyResolver;
    private final VehicleApplicationMapper mapper;

    @Override
    @Transactional
    public VehicleAppResponse create(CreateVehicleCommand command) {
        if (vehicleRepository.existsByPlate(command.plate())) {
            throw new ConflictException("Vehicle with plate " + command.plate() + " already exists");
        }
        String rawApiKey = Vehicle.generateRawApiKey();
        String apiKeyHash = Vehicle.hashApiKey(rawApiKey);

        Vehicle vehicle = new Vehicle(
                null,
                command.plate().toUpperCase().trim(),
                command.model(),
                command.capacityKg(),
                command.averageSpeedKmh() != null ? command.averageSpeedKmh() : new BigDecimal("40.00"),
                apiKeyHash,
                SoftDeleteStatus.ACTIVE
        );
        Vehicle saved = vehicleRepository.save(vehicle);
        return mapper.toResponseWithKey(saved, rawApiKey);
    }

    @Override
    @Transactional
    public VehicleAppResponse rotateApiKey(Integer vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", vehicleId));
        String rawApiKey = Vehicle.generateRawApiKey();
        vehicle.setApiKeyHash(Vehicle.hashApiKey(rawApiKey));
        Vehicle saved = vehicleRepository.save(vehicle);
        return mapper.toResponseWithKey(saved, rawApiKey);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleAppResponse findById(Integer id) {
        return vehicleRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<VehicleAppResponse> findAll(String plate, SoftDeleteStatus status, PageQuery pageQuery) {
        return vehicleRepository.findAll(plate, status, pageQuery).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public OptimizedRouteAppResponse optimizeRoute(OptimizeRouteCommand command) {
        Warehouse warehouse = warehouseRepository.findById(command.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", command.warehouseId()));
        if (warehouse.getLatitude() == null || warehouse.getLongitude() == null) {
            throw new BusinessRuleException("Warehouse " + warehouse.getId() + " does not have geographical coordinates");
        }

        Vehicle vehicle = vehicleRepository.findById(command.vehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", command.vehicleId()));

        GeoPoint origin = new GeoPoint(warehouse.getName(), warehouse.getLatitude(), warehouse.getLongitude(), null, BigDecimal.ZERO);
        List<GeoPoint> destinations = new ArrayList<>();

        for (Integer orderId : command.orderIds()) {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
            if (order.getDeliveryLatitude() == null || order.getDeliveryLongitude() == null) {
                throw new BusinessRuleException("Order " + order.getId() + " does not have delivery coordinates");
            }
            BigDecimal orderWeight = BigDecimal.ZERO;
            for (OrderDetail d : order.getDetails()) {
                Product p = productRepository.findById(d.getProductId()).orElse(null);
                if (p != null && p.getWeightKg() != null) {
                    orderWeight = orderWeight.add(p.getWeightKg().multiply(BigDecimal.valueOf(d.getQuantity())));
                }
            }
            destinations.add(new GeoPoint(
                    "Order #" + order.getOrderReference() + " (" + order.getCustomerName() + ")",
                    order.getDeliveryLatitude(),
                    order.getDeliveryLongitude(),
                    order.getId(),
                    orderWeight
            ));
        }

        RouteOptimizationStrategy strategy = strategyResolver.resolve(command.strategyName());
        OptimizedRoute route = strategy.optimize(new RouteOptimizationContext(origin, destinations, vehicle));
        return mapper.toRouteResponse(route);
    }
}

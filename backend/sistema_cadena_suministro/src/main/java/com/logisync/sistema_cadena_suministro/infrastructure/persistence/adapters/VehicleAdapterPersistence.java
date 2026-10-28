package com.logisync.sistema_cadena_suministro.infrastructure.persistence.adapters;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driving.VehicleRepository;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.models.Vehicle;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.PageSupport;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.VehicleDAO;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.VehicleEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers.VehiclePersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VehicleAdapterPersistence implements VehicleRepository {

    private final VehicleDAO dao;
    private final VehiclePersistenceMapper mapper;

    @Override
    public Vehicle save(Vehicle vehicle) {
        VehicleEntity entity;
        if (vehicle.getId() != null) {
            entity = dao.findById(vehicle.getId()).orElseGet(() -> mapper.toEntity(vehicle));
            entity.setPlate(vehicle.getPlate());
            entity.setModel(vehicle.getModel());
            entity.setCapacityKg(vehicle.getCapacityKg());
            entity.setAverageSpeedKmh(vehicle.getAverageSpeedKmh());
            entity.setApiKeyHash(vehicle.getApiKeyHash());
            entity.setStatus(vehicle.getStatus());
        } else {
            entity = mapper.toEntity(vehicle);
        }
        VehicleEntity saved = dao.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Vehicle> findById(Integer id) {
        return dao.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Vehicle> findByPlate(String plate) {
        return dao.findByPlate(plate).map(mapper::toDomain);
    }

    @Override
    public Optional<Vehicle> findByApiKeyHash(String apiKeyHash) {
        return dao.findByApiKeyHash(apiKeyHash).map(mapper::toDomain);
    }

    @Override
    public boolean existsByPlate(String plate) {
        return dao.existsByPlate(plate);
    }

    @Override
    public PageResult<Vehicle> findAll(String plate, SoftDeleteStatus status, PageQuery pageQuery) {
        Page<VehicleEntity> page = dao.search(plate, status, PageSupport.pageable(pageQuery));
        return PageSupport.toResult(page, mapper::toDomain);
    }
}

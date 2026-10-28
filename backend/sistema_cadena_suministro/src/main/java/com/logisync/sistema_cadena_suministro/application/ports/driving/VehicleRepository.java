package com.logisync.sistema_cadena_suministro.application.ports.driving;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.models.Vehicle;

import java.util.Optional;

public interface VehicleRepository {
    Vehicle save(Vehicle vehicle);
    Optional<Vehicle> findById(Integer id);
    Optional<Vehicle> findByPlate(String plate);
    Optional<Vehicle> findByApiKeyHash(String apiKeyHash);
    boolean existsByPlate(String plate);
    PageResult<Vehicle> findAll(String plate, SoftDeleteStatus status, PageQuery pageQuery);
}

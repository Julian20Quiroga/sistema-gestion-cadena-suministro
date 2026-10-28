package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.VehicleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleDAO extends JpaRepository<VehicleEntity, Integer> {

    Optional<VehicleEntity> findByPlate(String plate);

    Optional<VehicleEntity> findByApiKeyHash(String apiKeyHash);

    boolean existsByPlate(String plate);

    @Query("""
        SELECT v FROM VehicleEntity v
        WHERE (:plate IS NULL OR LOWER(v.plate) LIKE LOWER(CONCAT('%', :plate, '%')))
          AND (:status IS NULL OR v.status = :status)
        ORDER BY v.id DESC
    """)
    Page<VehicleEntity> search(
            @Param("plate") String plate,
            @Param("status") SoftDeleteStatus status,
            Pageable pageable
    );
}

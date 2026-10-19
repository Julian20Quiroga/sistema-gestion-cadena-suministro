package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.domain.enums.SupplyNeedStatus;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.SupplyNeedEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplyNeedDAO extends JpaRepository<SupplyNeedEntity, Integer> {

    boolean existsByProductIdAndStatus(Integer productId, SupplyNeedStatus status);

    Optional<SupplyNeedEntity> findFirstByProductIdAndStatusOrderByIdDesc(Integer productId, SupplyNeedStatus status);

    Page<SupplyNeedEntity> findByStatusOrderByIdDesc(SupplyNeedStatus status, Pageable pageable);
}

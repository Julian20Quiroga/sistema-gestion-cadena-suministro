package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderDAO extends JpaRepository<OrderEntity, Integer> {

    Optional<OrderEntity> findByOrderReference(String orderReference);

    boolean existsByOrderReference(String orderReference);

    @Query("""
        SELECT o FROM OrderEntity o
        WHERE (:reference IS NULL OR LOWER(o.orderReference) LIKE LOWER(CONCAT('%', :reference, '%')))
          AND (:status IS NULL OR o.status = :status)
        ORDER BY o.id DESC
    """)
    Page<OrderEntity> search(
            @Param("reference") String reference,
            @Param("status") OrderStatus status,
            Pageable pageable
    );
}

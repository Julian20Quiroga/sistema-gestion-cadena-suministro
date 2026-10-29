package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductDAO extends JpaRepository<ProductEntity, Integer>, JpaSpecificationExecutor<ProductEntity> {
    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Integer id);

    @Modifying
    @Query("update ProductEntity p set p.supplierId = null where p.supplierId = :supplierId")
    int clearSupplierReference(@Param("supplierId") Integer supplierId);
}





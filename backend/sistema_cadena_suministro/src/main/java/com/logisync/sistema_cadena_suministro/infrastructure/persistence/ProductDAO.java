package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductDAO extends JpaRepository<ProductEntity, Integer>, JpaSpecificationExecutor<ProductEntity> {
    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Integer id);
}

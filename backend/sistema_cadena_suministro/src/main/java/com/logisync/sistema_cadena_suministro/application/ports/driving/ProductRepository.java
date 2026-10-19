package com.logisync.sistema_cadena_suministro.application.ports.driving;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.queries.ProductFilter;
import com.logisync.sistema_cadena_suministro.domain.models.Product;

import java.util.Optional;

public interface ProductRepository {
    Product create(Product product);
    Product update(Product product, Integer id);
    void delete(Integer id);
    Optional<Product> findById(Integer id);
    PageResult<Product> findAll(ProductFilter filter, PageQuery pageQuery);
    boolean existsBySku(String sku);
    boolean existsBySkuExcluding(String sku, Integer id);
}

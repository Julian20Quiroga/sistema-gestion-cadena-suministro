package com.logisync.sistema_cadena_suministro.infrastructure.persistence.adapters;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driving.ProductRepository;
import com.logisync.sistema_cadena_suministro.application.queries.ProductFilter;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.models.Category;
import com.logisync.sistema_cadena_suministro.domain.models.Product;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.CategoryDAO;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.PageSupport;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.ProductDAO;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.ProductSpecifications;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.CategoryEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.ProductEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers.ProductPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ProductAdapterPersistence implements ProductRepository {

    private final ProductDAO productDAO;
    private final CategoryDAO categoryDAO;
    private final ProductPersistenceMapper mapper;

    @Override
    public Product create(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        entity.setStatus(SoftDeleteStatus.ACTIVE);
        entity.setCategories(managedCategories(product.getCategories()));
        return mapper.toDomain(productDAO.save(entity));
    }

    @Override
    public Product update(Product product, Integer id) {
        ProductEntity entity = productDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        mapper.updateEntity(product, entity);
        entity.setSupplierId(product.getSupplierId());
        entity.setCategories(managedCategories(product.getCategories()));
        return mapper.toDomain(productDAO.save(entity));
    }

    @Override
    public void delete(Integer id) {
        ProductEntity entity = productDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        productDAO.delete(entity);
    }

    @Override
    public Optional<Product> findById(Integer id) {
        return productDAO.findById(id).map(mapper::toDomain);
    }

    @Override
    public PageResult<Product> findAll(ProductFilter filter, PageQuery pageQuery) {
        return PageSupport.toResult(
                productDAO.findAll(ProductSpecifications.from(filter), PageSupport.pageable(pageQuery)),
                mapper::toDomain);
    }

    @Override
    public boolean existsBySku(String sku) {
        return productDAO.existsBySku(sku);
    }

    @Override
    public boolean existsBySkuExcluding(String sku, Integer id) {
        return productDAO.existsBySkuAndIdNot(sku, id);
    }

    private Set<CategoryEntity> managedCategories(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(categoryDAO.findAllById(categories.stream().map(Category::getId).toList()));
    }
}



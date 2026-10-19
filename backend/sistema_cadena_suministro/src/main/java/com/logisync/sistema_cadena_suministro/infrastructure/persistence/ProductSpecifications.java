package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.application.queries.ProductFilter;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.ProductEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<ProductEntity> from(ProductFilter filter) {
        Specification<ProductEntity> spec = (root, query, cb) -> cb.conjunction();
        if (filter == null) {
            return spec;
        }
        if (filter.name() != null && !filter.name().isBlank()) {
            String pattern = "%" + filter.name().trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), pattern));
        }
        if (filter.sku() != null && !filter.sku().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("sku"), filter.sku().trim()));
        }
        if (filter.supplierId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("supplierId"), filter.supplierId()));
        }
        if (filter.status() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), filter.status()));
        }
        if (filter.categoryId() != null) {
            spec = spec.and((root, query, cb) -> {
                query.distinct(true);
                return cb.equal(root.join("categories").get("id"), filter.categoryId());
            });
        }
        return spec;
    }
}

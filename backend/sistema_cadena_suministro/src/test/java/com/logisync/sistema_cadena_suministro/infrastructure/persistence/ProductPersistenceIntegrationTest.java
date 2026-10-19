package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.queries.ProductFilter;
import com.logisync.sistema_cadena_suministro.application.ports.driving.CategoryRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.ProductRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.SupplierRepository;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.models.Category;
import com.logisync.sistema_cadena_suministro.domain.models.CategoryGroup;
import com.logisync.sistema_cadena_suministro.domain.models.Product;
import com.logisync.sistema_cadena_suministro.domain.models.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class ProductPersistenceIntegrationTest {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    SupplierRepository supplierRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Test
    void createsFiltersAndSoftDeletesProducts() {
        Supplier supplier = supplierRepository.create(new Supplier(null, "900", "Acme", "3001", "a@acme.co", null));
        CategoryGroup group = categoryRepository.createGroup(new CategoryGroup(null, "Devices"));
        Category phones = categoryRepository.createCategory(new Category(null, "Phones", group.getId()));

        Product product = new Product();
        product.setName("Galaxy");
        product.setSupplierId(supplier.getId());
        product.setSku("SKU-GAL");
        product.setSalePrice(new BigDecimal("500.00"));
        product.setWeightKg(new BigDecimal("0.2"));
        product.setReorderPoint(3);
        product.setCategories(List.of(phones));
        Product saved = productRepository.create(product);

        assertEquals(SoftDeleteStatus.ACTIVE, saved.getStatus());
        assertEquals(1, saved.getCategories().size());
        assertTrue(productRepository.existsBySku("SKU-GAL"));
        assertFalse(productRepository.existsBySkuExcluding("SKU-GAL", saved.getId()));

        var byCategory = productRepository.findAll(
                new ProductFilter("gal", null, supplier.getId(), phones.getId(), SoftDeleteStatus.ACTIVE),
                PageQuery.of(0, 10));
        assertEquals(1, byCategory.totalElements());

        productRepository.delete(saved.getId());
        var active = productRepository.findAll(
                new ProductFilter(null, null, null, null, SoftDeleteStatus.ACTIVE), PageQuery.of(0, 10));
        assertEquals(0, active.totalElements());
    }
}

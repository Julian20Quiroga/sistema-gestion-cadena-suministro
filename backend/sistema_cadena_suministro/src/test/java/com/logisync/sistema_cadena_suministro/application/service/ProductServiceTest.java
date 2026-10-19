package com.logisync.sistema_cadena_suministro.application.service;

import com.logisync.sistema_cadena_suministro.application.commands.CreateProductCommand;
import com.logisync.sistema_cadena_suministro.application.mappers.CategoryApplicationMapper;
import com.logisync.sistema_cadena_suministro.application.mappers.ProductApplicationMapper;
import com.logisync.sistema_cadena_suministro.application.ports.driving.CategoryRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.ProductRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.SupplierRepository;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ConflictException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.models.Category;
import com.logisync.sistema_cadena_suministro.domain.models.Product;
import com.logisync.sistema_cadena_suministro.domain.models.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;
    @Mock
    SupplierRepository supplierRepository;
    @Mock
    CategoryRepository categoryRepository;

    ProductService service;

    @BeforeEach
    void setUp() {
        ProductApplicationMapper mapper = Mappers.getMapper(ProductApplicationMapper.class);
        ReflectionTestUtils.setField(mapper, "categoryApplicationMapper",
                Mappers.getMapper(CategoryApplicationMapper.class));
        service = new ProductService(mapper, productRepository, supplierRepository, categoryRepository);
    }

    private CreateProductCommand command(List<Integer> categoryIds) {
        return new CreateProductCommand("Phone", 1, "SKU-1", null, null, 8, 128, null, "black",
                new BigDecimal("999.99"), new BigDecimal("0.2"), 5, categoryIds);
    }

    @Test
    void createRejectsDuplicateSku() {
        when(productRepository.existsBySku("SKU-1")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.create(command(null)));
        verify(productRepository, never()).create(any());
    }

    @Test
    void createRejectsInactiveSupplier() {
        when(supplierRepository.findById(1))
                .thenReturn(Optional.of(new Supplier(1, "1", "S", "1", "a@b.c", SoftDeleteStatus.DELETED)));

        assertThrows(BusinessRuleException.class, () -> service.create(command(null)));
    }

    @Test
    void createRejectsUnknownCategory() {
        when(supplierRepository.findById(1))
                .thenReturn(Optional.of(new Supplier(1, "1", "S", "1", "a@b.c", SoftDeleteStatus.ACTIVE)));
        when(categoryRepository.findCategoriesByIds(any())).thenReturn(List.of(new Category(1, "A", 1)));

        assertThrows(ResourceNotFoundException.class, () -> service.create(command(List.of(1, 2))));
    }

    @Test
    void createPersistsProductWithResolvedCategories() {
        when(supplierRepository.findById(1))
                .thenReturn(Optional.of(new Supplier(1, "1", "S", "1", "a@b.c", SoftDeleteStatus.ACTIVE)));
        when(categoryRepository.findCategoriesByIds(any())).thenReturn(List.of(new Category(1, "Phones", 1)));
        when(productRepository.create(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p.setId(10);
            p.setStatus(SoftDeleteStatus.ACTIVE);
            return p;
        });

        var response = service.create(command(List.of(1)));

        assertEquals(10, response.id());
        assertEquals("Phones", response.categories().get(0).name());
    }
}

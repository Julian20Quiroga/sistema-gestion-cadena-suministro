package com.logisync.sistema_cadena_suministro.application.service;

import com.logisync.sistema_cadena_suministro.application.commands.CreateProductCommand;
import com.logisync.sistema_cadena_suministro.application.commands.UpdateProductCommand;
import com.logisync.sistema_cadena_suministro.application.mappers.ProductApplicationMapper;
import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreateProductUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.DeleteProductUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryProductUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.UpdateProductUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.CategoryRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.ProductRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.SupplierRepository;
import com.logisync.sistema_cadena_suministro.application.queries.ProductFilter;
import com.logisync.sistema_cadena_suministro.application.responses.ProductAppResponse;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ConflictException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.models.Category;
import com.logisync.sistema_cadena_suministro.domain.models.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService
        implements CreateProductUseCase, UpdateProductUseCase, DeleteProductUseCase, QueryProductUseCase {

    private final ProductApplicationMapper mapper;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ProductAppResponse create(CreateProductCommand command) {
        if (productRepository.existsBySku(command.sku())) {
            throw new ConflictException("SKU already registered: " + command.sku());
        }
        requireActiveSupplier(command.supplierId());
        Product product = mapper.toModelCreate(command);
        product.setCategories(resolveCategories(command.categoryIds()));
        return mapper.toResponse(productRepository.create(product));
    }

    @Override
    @Transactional
    public ProductAppResponse update(UpdateProductCommand command, Integer id) {
        productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", id));
        if (productRepository.existsBySkuExcluding(command.sku(), id)) {
            throw new ConflictException("SKU already registered: " + command.sku());
        }
        requireActiveSupplier(command.supplierId());
        Product product = mapper.toModelUpdate(command);
        product.setCategories(resolveCategories(command.categoryIds()));
        return mapper.toResponse(productRepository.update(product, id));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        productRepository.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductAppResponse findById(Integer id) {
        return productRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<ProductAppResponse> findAll(ProductFilter filter, PageQuery pageQuery) {
        return productRepository.findAll(filter, pageQuery).map(mapper::toResponse);
    }

    private void requireActiveSupplier(Integer supplierId) {
        boolean active = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", supplierId))
                .isActive();
        if (!active) {
            throw new BusinessRuleException("Supplier " + supplierId + " is not active");
        }
    }

    private List<Category> resolveCategories(Collection<Integer> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return List.of();
        }
        Set<Integer> distinctIds = Set.copyOf(categoryIds);
        List<Category> categories = categoryRepository.findCategoriesByIds(distinctIds);
        if (categories.size() != distinctIds.size()) {
            Set<Integer> found = Set.copyOf(categories.stream().map(Category::getId).toList());
            Integer missing = distinctIds.stream().filter(i -> !found.contains(i)).findFirst().orElse(null);
            throw new ResourceNotFoundException("Category", missing);
        }
        return categories;
    }
}

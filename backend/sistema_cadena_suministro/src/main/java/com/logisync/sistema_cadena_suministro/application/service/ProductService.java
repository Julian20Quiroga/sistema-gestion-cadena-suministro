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

/**
 * Servicio de aplicación para la gestión del catálogo maestro de productos.
 * Orquesta la creación, actualización, borrado lógico y consultas dinámicas con filtros JPA.
 */
@Service
@RequiredArgsConstructor
public class ProductService
        implements CreateProductUseCase, UpdateProductUseCase, DeleteProductUseCase, QueryProductUseCase {

    private final ProductApplicationMapper mapper;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Registra un nuevo producto verificando la unicidad del SKU y la validez del proveedor y categorías.
     */
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

    /**
     * Actualiza los datos de un producto existente validando colisiones de SKU y estado del proveedor.
     */
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

    /**
     * Aplica el borrado lógico del producto en la base de datos.
     */
    @Override
    @Transactional
    public void delete(Integer id) {
        productRepository.delete(id);
    }

    /**
     * Consulta un producto por su identificador único.
     */
    @Override
    @Transactional(readOnly = true)
    public ProductAppResponse findById(Integer id) {
        return productRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    /**
     * Lista productos de forma paginada admitiendo filtros dinámicos por nombre, SKU y categoría.
     */
    @Override
    @Transactional(readOnly = true)
    public PageResult<ProductAppResponse> findAll(ProductFilter filter, PageQuery pageQuery) {
        return productRepository.findAll(filter, pageQuery).map(mapper::toResponse);
    }

    /**
     * Valida que el proveedor asignado al producto exista y se encuentre en estado activo.
     */
    private void requireActiveSupplier(Integer supplierId) {
        boolean active = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", supplierId))
                .isActive();
        if (!active) {
            throw new BusinessRuleException("Supplier " + supplierId + " is not active");
        }
    }

    /**
     * Resuelve y valida la existencia de todas las categorías asociadas al producto.
     */
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

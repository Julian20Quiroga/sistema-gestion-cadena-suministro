package com.logisync.sistema_cadena_suministro.application.service;

import com.logisync.sistema_cadena_suministro.application.commands.CreateSupplierCommand;
import com.logisync.sistema_cadena_suministro.application.commands.UpdateSupplierCommand;
import com.logisync.sistema_cadena_suministro.application.mappers.SupplierApplicationMapper;
import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreateSupplierUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.DeleteSupplierUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QuerySupplierUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.UpdateSupplierUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.SupplierRepository;
import com.logisync.sistema_cadena_suministro.application.responses.SupplierAppResponse;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.models.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de aplicación para la gestión de proveedores en la cadena de suministro.
 */
@Service
@RequiredArgsConstructor
public class SupplierService
        implements CreateSupplierUseCase, UpdateSupplierUseCase, DeleteSupplierUseCase, QuerySupplierUseCase {

    private final SupplierApplicationMapper supplierApplicationMapper;
    private final SupplierRepository supplierRepository;

    /**
     * Registra un nuevo proveedor en la plataforma con sus índices de confiabilidad y lead time.
     */
    @Override
    @Transactional
    public SupplierAppResponse create(CreateSupplierCommand command) {
        Supplier supplier = supplierApplicationMapper.toModelCreate(command);
        return supplierApplicationMapper.toResponse(supplierRepository.create(supplier));
    }

    /**
     * Actualiza la información de contacto o métricas de un proveedor existente.
     */
    @Override
    @Transactional
    public SupplierAppResponse update(UpdateSupplierCommand command, Integer id) {
        Supplier supplier = supplierApplicationMapper.toModelUpdate(command);
        return supplierApplicationMapper.toResponse(supplierRepository.update(supplier, id));
    }

    /**
     * Realiza el borrado definitivo del proveedor.
     */
    @Override
    @Transactional
    public void delete(Integer id) {
        supplierRepository.delete(id);
    }

    /**
     * Consulta los detalles de un proveedor por su ID.
     */
    @Override
    @Transactional(readOnly = true)
    public SupplierAppResponse findById(Integer id) {
        return supplierRepository.findById(id)
                .map(supplierApplicationMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));
    }

    /**
     * Lista de manera paginada los proveedores aplicando filtros opcionales por nombre y estado.
     */
    @Override
    @Transactional(readOnly = true)
    public PageResult<SupplierAppResponse> findAll(String name, SoftDeleteStatus status, PageQuery pageQuery) {
        return supplierRepository.findAll(name, status, pageQuery).map(supplierApplicationMapper::toResponse);
    }
}


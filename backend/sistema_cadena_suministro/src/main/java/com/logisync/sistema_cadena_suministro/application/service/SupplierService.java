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

@Service
@RequiredArgsConstructor
public class SupplierService
        implements CreateSupplierUseCase, UpdateSupplierUseCase, DeleteSupplierUseCase, QuerySupplierUseCase {

    private final SupplierApplicationMapper supplierApplicationMapper;
    private final SupplierRepository supplierRepository;

    @Override
    @Transactional
    public SupplierAppResponse create(CreateSupplierCommand command) {
        Supplier supplier = supplierApplicationMapper.toModelCreate(command);
        return supplierApplicationMapper.toResponse(supplierRepository.create(supplier));
    }

    @Override
    @Transactional
    public SupplierAppResponse update(UpdateSupplierCommand command, Integer id) {
        Supplier supplier = supplierApplicationMapper.toModelUpdate(command);
        return supplierApplicationMapper.toResponse(supplierRepository.update(supplier, id));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        supplierRepository.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierAppResponse findById(Integer id) {
        return supplierRepository.findById(id)
                .map(supplierApplicationMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<SupplierAppResponse> findAll(String name, SoftDeleteStatus status, PageQuery pageQuery) {
        return supplierRepository.findAll(name, status, pageQuery).map(supplierApplicationMapper::toResponse);
    }

}

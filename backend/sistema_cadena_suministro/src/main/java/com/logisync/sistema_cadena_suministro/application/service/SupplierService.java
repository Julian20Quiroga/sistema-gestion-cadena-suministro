package com.logisync.sistema_cadena_suministro.application.service;

import com.logisync.sistema_cadena_suministro.application.commands.CreateSupplierCommand;
import com.logisync.sistema_cadena_suministro.application.commands.UpdateSupplierCommand;
import com.logisync.sistema_cadena_suministro.application.mappers.SupplierApplicationMapper;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreateSupplierUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.UpdateSupplierUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.SupplierRepository;
import com.logisync.sistema_cadena_suministro.application.responses.SupplierAppResponse;
import com.logisync.sistema_cadena_suministro.domain.models.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierService implements CreateSupplierUseCase, UpdateSupplierUseCase {

    private final SupplierApplicationMapper supplierApplicationMapper;
    private final SupplierRepository supplierRepository;

    @Override
    public SupplierAppResponse create(CreateSupplierCommand command) {
        Supplier supplier = supplierApplicationMapper.toModelCreate(command);
        return supplierApplicationMapper.toResponse(supplierRepository.create(supplier));
    }

    @Override
    public SupplierAppResponse update(UpdateSupplierCommand command, Integer id) {
        Supplier supplier = supplierApplicationMapper.toModelUpdate(command);
        log.info("el estado del proveedor es: " + supplier.getStatus());
        return supplierApplicationMapper.toResponse(supplierRepository.update(supplier, id));
    }

}
package com.logisync.sistema_cadena_suministro.application.service;

import com.logisync.sistema_cadena_suministro.application.commands.CreateSupplierCommand;
import com.logisync.sistema_cadena_suministro.application.mappers.SupplierApplicationMapper;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreateSupplierUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.SupplierRepository;
import com.logisync.sistema_cadena_suministro.application.responses.SupplierAppResponse;
import com.logisync.sistema_cadena_suministro.domain.models.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupplierService implements CreateSupplierUseCase {

    private final SupplierApplicationMapper supplierApplicationMapper;
    private final SupplierRepository supplierRepository;

    @Override
    public SupplierAppResponse create(CreateSupplierCommand command) {
        Supplier supplier = supplierApplicationMapper.toModel(command);
        return supplierApplicationMapper.toResponse(supplierRepository.create(supplier));
    }

}

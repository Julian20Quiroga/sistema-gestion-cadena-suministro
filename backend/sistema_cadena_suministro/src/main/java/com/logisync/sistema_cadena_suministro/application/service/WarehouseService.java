package com.logisync.sistema_cadena_suministro.application.service;

import com.logisync.sistema_cadena_suministro.application.commands.CreateWarehouseCommand;
import com.logisync.sistema_cadena_suministro.application.commands.UpdateWarehouseCommand;
import com.logisync.sistema_cadena_suministro.application.mappers.WarehouseApplicationMapper;
import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreateWarehouseUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.DeleteWarehouseUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryWarehouseUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.UpdateWarehouseUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.WarehouseRepository;
import com.logisync.sistema_cadena_suministro.application.responses.WarehouseAppResponse;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WarehouseService
        implements CreateWarehouseUseCase, UpdateWarehouseUseCase, DeleteWarehouseUseCase, QueryWarehouseUseCase {

    private final WarehouseApplicationMapper mapper;
    private final WarehouseRepository warehouseRepository;

    @Override
    @Transactional
    public WarehouseAppResponse create(CreateWarehouseCommand command) {
        return mapper.toResponse(warehouseRepository.create(mapper.toModelCreate(command)));
    }

    @Override
    @Transactional
    public WarehouseAppResponse update(UpdateWarehouseCommand command, Integer id) {
        return mapper.toResponse(warehouseRepository.update(mapper.toModelUpdate(command), id));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        warehouseRepository.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseAppResponse findById(Integer id) {
        return warehouseRepository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<WarehouseAppResponse> findAll(String name, SoftDeleteStatus status, PageQuery pageQuery) {
        return warehouseRepository.findAll(name, status, pageQuery).map(mapper::toResponse);
    }
}

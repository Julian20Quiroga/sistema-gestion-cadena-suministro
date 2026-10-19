package com.logisync.sistema_cadena_suministro.application.service;

import com.logisync.sistema_cadena_suministro.application.commands.CreateDispatchCommand;
import com.logisync.sistema_cadena_suministro.application.facade.DispatchFacade;
import com.logisync.sistema_cadena_suministro.application.mappers.DispatchApplicationMapper;
import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreateDispatchUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.ManageDispatchStatusUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryDispatchUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.DispatchRepository;
import com.logisync.sistema_cadena_suministro.application.responses.DispatchAppResponse;
import com.logisync.sistema_cadena_suministro.domain.enums.DispatchStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DispatchService implements CreateDispatchUseCase, ManageDispatchStatusUseCase, QueryDispatchUseCase {

    private final DispatchFacade dispatchFacade;
    private final DispatchRepository dispatchRepository;
    private final DispatchApplicationMapper mapper;

    @Override
    @Transactional
    public DispatchAppResponse create(CreateDispatchCommand command) {
        return dispatchFacade.createDispatchWithStockReservation(command);
    }

    @Override
    @Transactional
    public DispatchAppResponse startPreparation(Integer dispatchId) {
        return dispatchFacade.startPreparation(dispatchId);
    }

    @Override
    @Transactional
    public DispatchAppResponse ship(Integer dispatchId) {
        return dispatchFacade.shipAndConsumeStock(dispatchId);
    }

    @Override
    @Transactional
    public DispatchAppResponse deliver(Integer dispatchId) {
        return dispatchFacade.markDelivered(dispatchId);
    }

    @Override
    @Transactional
    public DispatchAppResponse cancel(Integer dispatchId) {
        return dispatchFacade.cancelAndReleaseStock(dispatchId);
    }

    @Override
    @Transactional(readOnly = true)
    public DispatchAppResponse findById(Integer id) {
        return dispatchRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Dispatch", id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<DispatchAppResponse> findAll(Integer warehouseId, Integer orderId,
                                                   DispatchStatus status, PageQuery pageQuery) {
        return dispatchRepository.findAll(warehouseId, orderId, status, pageQuery).map(mapper::toResponse);
    }
}

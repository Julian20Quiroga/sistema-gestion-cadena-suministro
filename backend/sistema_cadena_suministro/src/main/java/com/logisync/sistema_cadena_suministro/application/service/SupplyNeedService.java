package com.logisync.sistema_cadena_suministro.application.service;

import com.logisync.sistema_cadena_suministro.application.mappers.PurchaseOrderApplicationMapper;
import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driven.ManageSupplyNeedUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.SupplyNeedRepository;
import com.logisync.sistema_cadena_suministro.application.responses.SupplyNeedAppResponse;
import com.logisync.sistema_cadena_suministro.domain.enums.SupplyNeedStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.models.SupplyNeed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SupplyNeedService implements ManageSupplyNeedUseCase {

    private final SupplyNeedRepository supplyNeedRepository;
    private final PurchaseOrderApplicationMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public PageResult<SupplyNeedAppResponse> findAll(SupplyNeedStatus status, PageQuery pageQuery) {
        return supplyNeedRepository.findAll(status, pageQuery).map(mapper::toSupplyNeedResponse);
    }

    @Override
    @Transactional
    public SupplyNeedAppResponse dismiss(Integer id) {
        SupplyNeed need = supplyNeedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplyNeed", id));
        need.dismiss();
        return mapper.toSupplyNeedResponse(supplyNeedRepository.save(need));
    }
}

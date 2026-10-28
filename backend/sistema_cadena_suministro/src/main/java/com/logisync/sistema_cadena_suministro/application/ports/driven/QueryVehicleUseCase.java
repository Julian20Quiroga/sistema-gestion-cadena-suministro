package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.responses.VehicleAppResponse;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;

public interface QueryVehicleUseCase {
    VehicleAppResponse findById(Integer id);
    PageResult<VehicleAppResponse> findAll(String plate, SoftDeleteStatus status, PageQuery pageQuery);
}

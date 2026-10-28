package com.logisync.sistema_cadena_suministro.application.mappers;

import com.logisync.sistema_cadena_suministro.application.responses.DetailDispatchAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.DispatchAppResponse;
import com.logisync.sistema_cadena_suministro.domain.models.DetailDispatch;
import com.logisync.sistema_cadena_suministro.domain.models.Dispatch;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DispatchApplicationMapper {
    DispatchAppResponse toResponse(Dispatch dispatch);
    DetailDispatchAppResponse toDetailResponse(DetailDispatch detail);
}

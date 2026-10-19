package com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers;

import com.logisync.sistema_cadena_suministro.application.responses.LayoutOptimizationAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.LayoutSnapshotAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.RelocationProposalAppResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.LayoutOptimizationResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.LayoutSnapshotResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.RelocationProposalResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StorageOptimizationRestMapper {
    LayoutSnapshotResponse toResponse(LayoutSnapshotAppResponse app);
    RelocationProposalResponse toResponse(RelocationProposalAppResponse app);
    LayoutOptimizationResponse toResponse(LayoutOptimizationAppResponse app);
}

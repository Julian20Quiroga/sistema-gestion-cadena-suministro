package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

import java.util.List;

public record LayoutOptimizationResponse(
        Integer warehouseId,
        Integer preOptimizationSnapshotId,
        List<RelocationProposalResponse> proposals,
        String summary
) {}

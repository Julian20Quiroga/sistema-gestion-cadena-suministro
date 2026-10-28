package com.logisync.sistema_cadena_suministro.application.responses;

import java.util.List;

public record LayoutOptimizationAppResponse(
        Integer warehouseId,
        Integer preOptimizationSnapshotId,
        List<RelocationProposalAppResponse> proposals,
        String summary
) {}

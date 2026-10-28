package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

public record RelocationProposalResponse(
        Integer productId,
        String productSku,
        Integer fromLocationId,
        String fromLocationName,
        Integer toLocationId,
        String toLocationName,
        int quantity,
        String abcClassification,
        String reason
) {}

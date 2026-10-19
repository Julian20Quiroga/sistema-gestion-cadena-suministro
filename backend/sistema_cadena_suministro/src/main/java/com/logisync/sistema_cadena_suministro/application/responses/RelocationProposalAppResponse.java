package com.logisync.sistema_cadena_suministro.application.responses;

public record RelocationProposalAppResponse(
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

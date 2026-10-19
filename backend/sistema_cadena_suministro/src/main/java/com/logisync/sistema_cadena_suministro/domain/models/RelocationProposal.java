package com.logisync.sistema_cadena_suministro.domain.models;

public record RelocationProposal(
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

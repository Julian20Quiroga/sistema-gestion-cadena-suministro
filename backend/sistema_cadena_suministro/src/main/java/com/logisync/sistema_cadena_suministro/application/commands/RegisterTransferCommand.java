package com.logisync.sistema_cadena_suministro.application.commands;

public record RegisterTransferCommand(Integer sourceSpaceId, Integer targetSpaceId, Integer productId, int quantity,
        String reference) {
}

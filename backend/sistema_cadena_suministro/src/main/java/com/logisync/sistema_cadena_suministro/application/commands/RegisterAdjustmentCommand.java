package com.logisync.sistema_cadena_suministro.application.commands;

public record RegisterAdjustmentCommand(Integer storageSpaceId, Integer productId, int delta, String reason) {
}

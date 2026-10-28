package com.logisync.sistema_cadena_suministro.application.commands;

public record RegisterExitCommand(Integer storageSpaceId, Integer productId, int quantity, String reference) {
}

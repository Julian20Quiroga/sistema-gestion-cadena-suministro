package com.logisync.sistema_cadena_suministro.application.commands;

public record RegisterEntryCommand(Integer storageSpaceId, Integer productId, int quantity, String reference) {
}

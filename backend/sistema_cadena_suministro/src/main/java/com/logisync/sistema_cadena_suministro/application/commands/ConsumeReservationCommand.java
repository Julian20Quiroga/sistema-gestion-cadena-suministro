package com.logisync.sistema_cadena_suministro.application.commands;

public record ConsumeReservationCommand(Integer inventoryId, int quantity, String reference) {
}

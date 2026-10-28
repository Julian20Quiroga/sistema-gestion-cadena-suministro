package com.logisync.sistema_cadena_suministro.application.events;

public record StockLowEvent(Integer productId, long availableQuantity, int reorderPoint) implements DomainEvent {
}

package com.logisync.sistema_cadena_suministro.application.events;

import java.util.List;

public record StockMovementRecordedEvent(Integer movementId, String movementType, String reference,
        List<ProductQuantity> products) implements DomainEvent {

    public record ProductQuantity(Integer productId, Integer storageSpaceId, int quantity) {
    }
}

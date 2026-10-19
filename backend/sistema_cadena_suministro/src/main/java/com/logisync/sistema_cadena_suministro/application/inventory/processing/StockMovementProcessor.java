package com.logisync.sistema_cadena_suministro.application.inventory.processing;

import com.logisync.sistema_cadena_suministro.application.events.StockMovementRecordedEvent;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryMovement;
import com.logisync.sistema_cadena_suministro.domain.inventory.command.StockMovementCommand;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * Template Method: fixed movement flow (validate, apply, persist, audit, notify);
 * each movement type supplies its own validation and command.
 */
@Slf4j
public abstract class StockMovementProcessor<R> {

    protected final MovementContext context;

    protected StockMovementProcessor(MovementContext context) {
        this.context = context;
    }

    public final InventoryMovement process(R request) {
        validate(request);
        StockMovementCommand command = buildCommand(request);
        command.execute();
        InventoryMovement movement = persist(command, request);
        audit(movement);
        notifyObservers(movement, command);
        return movement;
    }

    protected abstract void validate(R request);

    protected abstract StockMovementCommand buildCommand(R request);

    protected abstract String reasonOf(R request);

    protected abstract String referenceOf(R request);

    protected void audit(InventoryMovement movement) {
        log.info("Inventory movement {} type={} reference={}", movement.getId(), movement.getType(),
                movement.getReference());
    }

    protected void notifyObservers(InventoryMovement movement, StockMovementCommand command) {
        publishRecorded(movement);
        command.items().stream().map(InventoryItem::getProductId).distinct()
                .forEach(context.getLowStockMonitor()::check);
    }

    protected final void publishRecorded(InventoryMovement movement) {
        context.getEventPublisher().publish(new StockMovementRecordedEvent(movement.getId(),
                movement.getType().code(), movement.getReference(),
                movement.getDetails().stream().map(d -> new StockMovementRecordedEvent.ProductQuantity(
                        d.productId(), d.storageSpaceId(), d.quantity())).toList()));
    }

    private InventoryMovement persist(StockMovementCommand command, R request) {
        command.items().forEach(context.getInventoryRepository()::save);
        InventoryMovement movement = InventoryMovement.from(command, LocalDateTime.now(context.getClock()),
                reasonOf(request), referenceOf(request));
        return context.getMovementRepository().save(movement);
    }
}

package com.logisync.sistema_cadena_suministro.application.inventory.processing;

import com.logisync.sistema_cadena_suministro.application.commands.RegisterEntryCommand;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryMovement;
import com.logisync.sistema_cadena_suministro.domain.inventory.command.EntryCommand;
import com.logisync.sistema_cadena_suministro.domain.inventory.command.StockMovementCommand;
import org.springframework.stereotype.Component;

/** Template Method step set for stock entries (no low-stock check: stock only grows). */
@Component
public class EntryMovementProcessor extends StockMovementProcessor<RegisterEntryCommand> {

    private final LocationRules rules;

    public EntryMovementProcessor(MovementContext context, LocationRules rules) {
        super(context);
        this.rules = rules;
    }

    @Override
    protected void validate(RegisterEntryCommand request) {
        LocationRules.requirePositive(request.quantity());
        rules.requireActiveProduct(request.productId());
        rules.requireCapacityFor(rules.requireLocation(request.storageSpaceId()), request.quantity());
    }

    @Override
    protected StockMovementCommand buildCommand(RegisterEntryCommand request) {
        InventoryItem item = context.getInventoryRepository()
                .findOrCreate(request.storageSpaceId(), request.productId());
        return new EntryCommand(item, request.quantity());
    }

    @Override
    protected String reasonOf(RegisterEntryCommand request) {
        return null;
    }

    @Override
    protected String referenceOf(RegisterEntryCommand request) {
        return request.reference();
    }

    @Override
    protected void notifyObservers(InventoryMovement movement, StockMovementCommand command) {
        publishRecorded(movement);
    }
}

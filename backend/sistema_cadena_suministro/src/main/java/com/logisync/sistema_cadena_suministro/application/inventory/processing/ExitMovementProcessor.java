package com.logisync.sistema_cadena_suministro.application.inventory.processing;

import com.logisync.sistema_cadena_suministro.application.commands.RegisterExitCommand;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.inventory.command.ExitCommand;
import com.logisync.sistema_cadena_suministro.domain.inventory.command.StockMovementCommand;
import org.springframework.stereotype.Component;

/** Template Method step set for manual stock exits from available stock. */
@Component
public class ExitMovementProcessor extends StockMovementProcessor<RegisterExitCommand> {

    public ExitMovementProcessor(MovementContext context) {
        super(context);
    }

    @Override
    protected void validate(RegisterExitCommand request) {
        LocationRules.requirePositive(request.quantity());
        InventoryItem item = find(request);
        if (item.available() < request.quantity()) {
            throw new BusinessRuleException("Insufficient available stock: available " + item.available()
                    + ", requested " + request.quantity());
        }
    }

    @Override
    protected StockMovementCommand buildCommand(RegisterExitCommand request) {
        return new ExitCommand(find(request), request.quantity());
    }

    @Override
    protected String reasonOf(RegisterExitCommand request) {
        return null;
    }

    @Override
    protected String referenceOf(RegisterExitCommand request) {
        return request.reference();
    }

    private InventoryItem find(RegisterExitCommand request) {
        return context.getInventoryRepository()
                .findBySpaceAndProduct(request.storageSpaceId(), request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("No stock of product " + request.productId()
                        + " at storage space " + request.storageSpaceId()));
    }
}

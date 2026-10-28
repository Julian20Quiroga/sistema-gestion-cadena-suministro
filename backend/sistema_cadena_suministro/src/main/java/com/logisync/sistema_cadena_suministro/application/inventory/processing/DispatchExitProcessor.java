package com.logisync.sistema_cadena_suministro.application.inventory.processing;

import com.logisync.sistema_cadena_suministro.application.commands.ConsumeReservationCommand;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.inventory.command.DispatchExitCommand;
import com.logisync.sistema_cadena_suministro.domain.inventory.command.StockMovementCommand;
import org.springframework.stereotype.Component;

/** Template Method step set for the exit of a confirmed dispatch (consumes reserved stock). */
@Component
public class DispatchExitProcessor extends StockMovementProcessor<ConsumeReservationCommand> {

    public DispatchExitProcessor(MovementContext context) {
        super(context);
    }

    @Override
    protected void validate(ConsumeReservationCommand request) {
        LocationRules.requirePositive(request.quantity());
        if (find(request).getReservedQuantity() < request.quantity()) {
            throw new BusinessRuleException("Not enough reserved stock to dispatch " + request.quantity()
                    + " units");
        }
    }

    @Override
    protected StockMovementCommand buildCommand(ConsumeReservationCommand request) {
        return new DispatchExitCommand(find(request), request.quantity());
    }

    @Override
    protected String reasonOf(ConsumeReservationCommand request) {
        return null;
    }

    @Override
    protected String referenceOf(ConsumeReservationCommand request) {
        return request.reference();
    }

    private InventoryItem find(ConsumeReservationCommand request) {
        return context.getInventoryRepository().findById(request.inventoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", request.inventoryId()));
    }
}

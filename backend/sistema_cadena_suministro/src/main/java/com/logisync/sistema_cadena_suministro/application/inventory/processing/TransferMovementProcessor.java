package com.logisync.sistema_cadena_suministro.application.inventory.processing;

import com.logisync.sistema_cadena_suministro.application.commands.RegisterTransferCommand;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.inventory.command.StockMovementCommand;
import com.logisync.sistema_cadena_suministro.domain.inventory.command.TransferCommand;
import com.logisync.sistema_cadena_suministro.domain.models.StorageSpace;
import org.springframework.stereotype.Component;

/** Template Method step set for transfers between two locations of the same warehouse. */
@Component
public class TransferMovementProcessor extends StockMovementProcessor<RegisterTransferCommand> {

    private final LocationRules rules;

    public TransferMovementProcessor(MovementContext context, LocationRules rules) {
        super(context);
        this.rules = rules;
    }

    @Override
    protected void validate(RegisterTransferCommand request) {
        LocationRules.requirePositive(request.quantity());
        if (request.sourceSpaceId().equals(request.targetSpaceId())) {
            throw new BusinessRuleException("Source and target locations must differ");
        }
        StorageSpace source = rules.requireLocation(request.sourceSpaceId());
        StorageSpace target = rules.requireLocation(request.targetSpaceId());
        if (!source.getWarehouseId().equals(target.getWarehouseId())) {
            throw new BusinessRuleException("Transfers are only allowed within the same warehouse");
        }
        rules.requireCapacityFor(target, request.quantity());
        InventoryItem sourceItem = source(request);
        if (sourceItem.available() < request.quantity()) {
            throw new BusinessRuleException("Insufficient available stock at source: available "
                    + sourceItem.available() + ", requested " + request.quantity());
        }
    }

    @Override
    protected StockMovementCommand buildCommand(RegisterTransferCommand request) {
        InventoryItem target = context.getInventoryRepository()
                .findOrCreate(request.targetSpaceId(), request.productId());
        return new TransferCommand(source(request), target, request.quantity());
    }

    @Override
    protected String reasonOf(RegisterTransferCommand request) {
        return null;
    }

    @Override
    protected String referenceOf(RegisterTransferCommand request) {
        return request.reference();
    }

    private InventoryItem source(RegisterTransferCommand request) {
        return context.getInventoryRepository()
                .findBySpaceAndProduct(request.sourceSpaceId(), request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("No stock of product " + request.productId()
                        + " at storage space " + request.sourceSpaceId()));
    }
}

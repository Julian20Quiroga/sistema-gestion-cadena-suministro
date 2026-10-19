package com.logisync.sistema_cadena_suministro.domain.inventory.command;

import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.inventory.MovementTypeFactory;

import java.util.List;

/** Command: moves units between two locations in one atomic movement. */
public class TransferCommand extends StockMovementCommand {

    public TransferCommand(InventoryItem source, InventoryItem target, int quantity) {
        super(MovementTypeFactory.TRANSFER,
                List.of(new ItemDelta(source, -quantity, 0), new ItemDelta(target, quantity, 0)));
    }
}

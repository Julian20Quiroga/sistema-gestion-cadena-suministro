package com.logisync.sistema_cadena_suministro.domain.inventory.command;

import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.inventory.MovementTypeFactory;

import java.util.List;

/** Command: exit of a dispatch, consuming previously reserved stock. */
public class DispatchExitCommand extends StockMovementCommand {

    public DispatchExitCommand(InventoryItem item, int quantity) {
        super(MovementTypeFactory.EXIT, List.of(new ItemDelta(item, -quantity, -quantity)));
    }
}

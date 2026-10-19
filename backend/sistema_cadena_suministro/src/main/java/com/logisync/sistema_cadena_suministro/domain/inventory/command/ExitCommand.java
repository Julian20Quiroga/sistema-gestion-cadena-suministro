package com.logisync.sistema_cadena_suministro.domain.inventory.command;

import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.inventory.MovementTypeFactory;

import java.util.List;

/** Command: goods leaving a location from available (unreserved) stock. */
public class ExitCommand extends StockMovementCommand {

    public ExitCommand(InventoryItem item, int quantity) {
        super(MovementTypeFactory.EXIT, List.of(new ItemDelta(item, -quantity, 0)));
    }
}

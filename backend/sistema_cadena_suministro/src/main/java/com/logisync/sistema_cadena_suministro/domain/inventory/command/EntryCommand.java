package com.logisync.sistema_cadena_suministro.domain.inventory.command;

import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.inventory.MovementTypeFactory;

import java.util.List;

/** Command: goods entering a location. */
public class EntryCommand extends StockMovementCommand {

    public EntryCommand(InventoryItem item, int quantity) {
        super(MovementTypeFactory.ENTRY, List.of(new ItemDelta(item, quantity, 0)));
    }
}

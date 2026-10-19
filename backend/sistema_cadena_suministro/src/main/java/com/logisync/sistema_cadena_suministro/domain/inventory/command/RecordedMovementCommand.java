package com.logisync.sistema_cadena_suministro.domain.inventory.command;

import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryMovement;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;

import java.util.List;
import java.util.Map;

/** Command rebuilt from the movement history so a past movement can be undone. */
public class RecordedMovementCommand extends StockMovementCommand {

    public RecordedMovementCommand(InventoryMovement movement, Map<Integer, InventoryItem> itemsById) {
        super(movement.getType(), List.copyOf(movement.toDeltas(itemsById)));
    }
}

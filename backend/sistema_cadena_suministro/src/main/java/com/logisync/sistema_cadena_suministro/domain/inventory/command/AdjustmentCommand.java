package com.logisync.sistema_cadena_suministro.domain.inventory.command;

import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.inventory.MovementTypeFactory;

import java.util.List;

/** Command: signed correction of physical stock; always requires a reason. */
public class AdjustmentCommand extends StockMovementCommand {

    public AdjustmentCommand(InventoryItem item, int delta, String reason) {
        super(MovementTypeFactory.ADJUSTMENT, List.of(new ItemDelta(item, delta, 0)));
        if (reason == null || reason.isBlank()) {
            throw new BusinessRuleException("An adjustment requires a reason");
        }
    }
}

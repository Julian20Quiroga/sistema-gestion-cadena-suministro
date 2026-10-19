package com.logisync.sistema_cadena_suministro.domain.inventory.command;

import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.inventory.MovementType;

import java.util.List;

/** Command: a reversible stock variation expressed as per-item deltas. */
public abstract class StockMovementCommand {

    private final MovementType type;
    private final List<ItemDelta> deltas;

    protected StockMovementCommand(MovementType type, List<ItemDelta> deltas) {
        this.type = type;
        this.deltas = List.copyOf(deltas);
    }

    public void execute() {
        deltas.forEach(d -> d.item().apply(d.physical(), d.reserved()));
    }

    public void undo() {
        for (int i = deltas.size() - 1; i >= 0; i--) {
            ItemDelta d = deltas.get(i);
            d.item().apply(-d.physical(), -d.reserved());
        }
    }

    public MovementType type() {
        return type;
    }

    public List<ItemDelta> deltas() {
        return deltas;
    }

    public List<InventoryItem> items() {
        return deltas.stream().map(ItemDelta::item).distinct().toList();
    }
}

package com.logisync.sistema_cadena_suministro.domain.inventory;

import com.logisync.sistema_cadena_suministro.domain.inventory.command.ItemDelta;
import com.logisync.sistema_cadena_suministro.domain.inventory.command.StockMovementCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/** Audit record of a stock variation; quantities are signed physical deltas. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovement {
    private Integer id;
    private MovementType type;
    private LocalDateTime date;
    private String reason;
    private String reference;
    private MovementStatus status;
    private List<MovementDetail> details;

    public static InventoryMovement from(StockMovementCommand command, LocalDateTime date, String reason,
            String reference) {
        List<MovementDetail> details = command.deltas().stream()
                .map(d -> new MovementDetail(d.item().getId(), d.item().getProductId(), d.item().getStorageSpaceId(),
                        d.physical(), d.reserved()))
                .toList();
        return new InventoryMovement(null, command.type(), date, reason, reference, MovementStatus.APPLIED, details);
    }

    public void markReversed() {
        this.status = MovementStatus.REVERSED;
    }

    public List<ItemDelta> toDeltas(java.util.Map<Integer, InventoryItem> itemsById) {
        return details.stream()
                .map(d -> new ItemDelta(itemsById.get(d.inventoryId()), d.quantity(), d.reservedDelta()))
                .toList();
    }
}

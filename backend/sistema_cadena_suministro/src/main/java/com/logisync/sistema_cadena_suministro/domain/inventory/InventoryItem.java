package com.logisync.sistema_cadena_suministro.domain.inventory;

import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Stock of one product in one storage location. Physical stock never goes negative nor below the reserved stock. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItem {
    private Integer id;
    private Integer productId;
    private Integer storageSpaceId;
    private int physicalQuantity;
    private int reservedQuantity;
    private Integer version;
    private Integer warehouseId;

    public int available() {
        return physicalQuantity - reservedQuantity;
    }

    public void apply(int physicalDelta, int reservedDelta) {
        int physical = physicalQuantity + physicalDelta;
        int reserved = reservedQuantity + reservedDelta;
        if (physical < 0) {
            throw new BusinessRuleException("Stock cannot be negative for product " + productId + " at space "
                    + storageSpaceId);
        }
        if (reserved < 0 || reserved > physical) {
            throw new BusinessRuleException("Reserved stock out of range for product " + productId + " at space "
                    + storageSpaceId + " (physical " + physical + ", reserved " + reserved + ")");
        }
        this.physicalQuantity = physical;
        this.reservedQuantity = reserved;
    }
}

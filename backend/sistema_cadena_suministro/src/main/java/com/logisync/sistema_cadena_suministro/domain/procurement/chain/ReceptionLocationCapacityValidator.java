package com.logisync.sistema_cadena_suministro.domain.procurement.chain;

import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.models.StorageSpace;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Chain of Responsibility: Validates reception storage locations belong to the warehouse and have capacity.
 */
public class ReceptionLocationCapacityValidator extends ReceptionValidator {

    @Override
    protected void check(ReceptionValidationContext context) {
        Map<Integer, Integer> incomingPerLocation = context.items().stream()
                .collect(Collectors.groupingBy(ReceptionItem::storageSpaceId, Collectors.summingInt(ReceptionItem::quantity)));

        for (var entry : incomingPerLocation.entrySet()) {
            Integer spaceId = entry.getKey();
            int incomingQty = entry.getValue();

            StorageSpace location = context.locationsById().get(spaceId);
            if (location == null || !location.isActive() || location.getType() != StorageSpaceType.LOCATION) {
                throw new BusinessRuleException("Storage space " + spaceId + " is not an active LOCATION");
            }
            if (!location.getWarehouseId().equals(context.order().getWarehouseId())) {
                throw new BusinessRuleException("Location " + spaceId + " belongs to warehouse "
                        + location.getWarehouseId() + ", expected warehouse " + context.order().getWarehouseId());
            }
            long currentUnits = context.currentUnitsInLocation().getOrDefault(spaceId, 0L);
            long freeCapacity = location.getCapacity() - currentUnits;
            if (incomingQty > freeCapacity) {
                throw new BusinessRuleException("Location " + spaceId + " does not have enough capacity. Free: "
                        + freeCapacity + ", incoming: " + incomingQty);
            }
        }
    }
}

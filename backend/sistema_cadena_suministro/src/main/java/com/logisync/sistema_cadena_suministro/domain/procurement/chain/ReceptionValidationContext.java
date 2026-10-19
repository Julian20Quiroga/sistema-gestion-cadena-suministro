package com.logisync.sistema_cadena_suministro.domain.procurement.chain;

import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrder;
import com.logisync.sistema_cadena_suministro.domain.models.StorageSpace;

import java.util.List;
import java.util.Map;

public record ReceptionValidationContext(
        PurchaseOrder order,
        List<ReceptionItem> items,
        Map<Integer, StorageSpace> locationsById,
        Map<Integer, Long> currentUnitsInLocation
) {}

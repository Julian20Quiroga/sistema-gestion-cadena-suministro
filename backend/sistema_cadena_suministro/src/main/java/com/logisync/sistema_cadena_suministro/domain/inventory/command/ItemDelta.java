package com.logisync.sistema_cadena_suministro.domain.inventory.command;

import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;

public record ItemDelta(InventoryItem item, int physical, int reserved) {
}

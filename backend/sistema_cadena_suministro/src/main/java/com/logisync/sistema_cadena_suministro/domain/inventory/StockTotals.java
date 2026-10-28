package com.logisync.sistema_cadena_suministro.domain.inventory;

public record StockTotals(long physical, long reserved) {

    public long available() {
        return physical - reserved;
    }
}

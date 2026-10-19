package com.logisync.sistema_cadena_suministro.application.ports.driven;

public interface ReleaseStockUseCase {
    void release(Integer inventoryId, int quantity);
}

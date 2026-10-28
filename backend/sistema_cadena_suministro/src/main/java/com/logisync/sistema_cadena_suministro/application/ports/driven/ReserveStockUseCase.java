package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.responses.StockAllocationAppResponse;

public interface ReserveStockUseCase {
    StockAllocationAppResponse reserve(Integer warehouseId, Integer productId, int quantity);
}

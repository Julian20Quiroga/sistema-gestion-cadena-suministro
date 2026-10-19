package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.responses.LayoutOptimizationAppResponse;

public interface OptimizeWarehouseLayoutUseCase {
    LayoutOptimizationAppResponse optimize(Integer warehouseId, boolean autoCreateSnapshot);
}

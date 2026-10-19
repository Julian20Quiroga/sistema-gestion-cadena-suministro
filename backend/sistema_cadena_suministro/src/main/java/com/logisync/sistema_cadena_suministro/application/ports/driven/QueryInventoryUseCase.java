package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.responses.InventoryLevelAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.ProductAvailabilityAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.ProductRotationAppResponse;

public interface QueryInventoryUseCase {
    PageResult<InventoryLevelAppResponse> findLevels(Integer productId, Integer warehouseId, Integer storageSpaceId,
            PageQuery pageQuery);

    ProductAvailabilityAppResponse availability(Integer productId);

    ProductRotationAppResponse rotation(Integer productId, int days);
}

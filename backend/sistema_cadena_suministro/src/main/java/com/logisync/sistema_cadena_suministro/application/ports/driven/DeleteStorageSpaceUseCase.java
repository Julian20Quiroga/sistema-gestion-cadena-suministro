package com.logisync.sistema_cadena_suministro.application.ports.driven;

public interface DeleteStorageSpaceUseCase {
    void delete(Integer warehouseId, Integer spaceId);
}

package com.logisync.sistema_cadena_suministro.application.mappers;

import com.logisync.sistema_cadena_suministro.application.responses.FreeLocationAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.StorageNodeAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.StorageSpaceAppResponse;
import com.logisync.sistema_cadena_suministro.domain.models.StorageSpace;
import com.logisync.sistema_cadena_suministro.domain.storage.LocationNode;
import com.logisync.sistema_cadena_suministro.domain.storage.StorageNodeView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StorageApplicationMapper {

    StorageSpaceAppResponse toResponse(StorageSpace space);

    StorageNodeAppResponse toResponse(StorageNodeView view);

    default FreeLocationAppResponse toResponse(LocationNode location) {
        return new FreeLocationAppResponse(location.id(), location.name(), location.capacity(),
                location.occupancy(), location.freeCapacity());
    }
}

package com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers;

import com.logisync.sistema_cadena_suministro.application.commands.CreateProductCommand;
import com.logisync.sistema_cadena_suministro.application.commands.UpdateProductCommand;
import com.logisync.sistema_cadena_suministro.application.responses.ProductAppResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreateProductRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.UpdateProductRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.ProductResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CategoryRestMapper.class)
public interface ProductRestMapper {

    CreateProductCommand toCommandCreate(CreateProductRequest request);

    UpdateProductCommand toCommandUpdate(UpdateProductRequest request);

    ProductResponse toResponse(ProductAppResponse response);
}

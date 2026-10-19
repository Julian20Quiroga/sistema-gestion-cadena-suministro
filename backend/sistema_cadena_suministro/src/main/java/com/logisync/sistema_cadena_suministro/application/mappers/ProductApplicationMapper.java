package com.logisync.sistema_cadena_suministro.application.mappers;

import com.logisync.sistema_cadena_suministro.application.commands.CreateProductCommand;
import com.logisync.sistema_cadena_suministro.application.commands.UpdateProductCommand;
import com.logisync.sistema_cadena_suministro.application.responses.ProductAppResponse;
import com.logisync.sistema_cadena_suministro.domain.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CategoryApplicationMapper.class)
public interface ProductApplicationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Product toModelCreate(CreateProductCommand command);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Product toModelUpdate(UpdateProductCommand command);

    ProductAppResponse toResponse(Product product);
}

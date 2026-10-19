package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.CreateProductCommand;
import com.logisync.sistema_cadena_suministro.application.responses.ProductAppResponse;

public interface CreateProductUseCase {
    ProductAppResponse create(CreateProductCommand command);
}

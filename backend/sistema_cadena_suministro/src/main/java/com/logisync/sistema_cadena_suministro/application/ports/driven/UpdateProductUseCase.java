package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.UpdateProductCommand;
import com.logisync.sistema_cadena_suministro.application.responses.ProductAppResponse;

public interface UpdateProductUseCase {
    ProductAppResponse update(UpdateProductCommand command, Integer id);
}

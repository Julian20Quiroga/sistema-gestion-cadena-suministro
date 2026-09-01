package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.CreateSupplierCommand;
import com.logisync.sistema_cadena_suministro.application.responses.SupplierAppResponse;

public interface CreateSupplierUseCase {
    SupplierAppResponse create(CreateSupplierCommand command);
}

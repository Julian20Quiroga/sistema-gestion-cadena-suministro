package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.UpdateSupplierCommand;
import com.logisync.sistema_cadena_suministro.application.responses.SupplierAppResponse;

public interface UpdateSupplierUseCase {
    SupplierAppResponse update(UpdateSupplierCommand command, Integer id);
}

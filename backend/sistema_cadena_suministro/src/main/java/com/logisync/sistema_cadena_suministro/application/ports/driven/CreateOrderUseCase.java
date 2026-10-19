package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.CreateOrderCommand;
import com.logisync.sistema_cadena_suministro.application.responses.OrderAppResponse;

public interface CreateOrderUseCase {
    OrderAppResponse create(CreateOrderCommand command);
}

package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.responses.OrderAppResponse;

public interface UpdateOrderStatusUseCase {
    OrderAppResponse startPreparation(Integer orderId);
    OrderAppResponse markDispatched(Integer orderId);
    OrderAppResponse markDelivered(Integer orderId);
    OrderAppResponse cancel(Integer orderId);
}

package com.logisync.sistema_cadena_suministro.domain.orders.state;

import com.logisync.sistema_cadena_suministro.domain.enums.OrderStatus;
import com.logisync.sistema_cadena_suministro.domain.models.Order;

/**
 * Interfaz de estado para pedidos de clientes.
 * Patrón aplicado: State (modula el comportamiento de la orden según su fase:
 * REGISTRADO -> PREPARACION -> EN_RUTA -> ENTREGADO o CANCELADO).
 */
public interface OrderState {

    /**
     * Retorna el enumerado correspondiente al estado de la orden.
     */
    OrderStatus getStatus();

    /**
     * Inicia la fase de alistamiento y empaque del pedido en bodega.
     */
    void startPreparation(Order order);

    /**
     * Marca el pedido como despachado y en ruta hacia el cliente.
     */
    void markDispatched(Order order);

    /**
     * Confirma la entrega efectiva del pedido en el domicilio del cliente.
     */
    void markDelivered(Order order);

    /**
     * Cancela el pedido liberando cualquier reserva asociada.
     */
    void cancel(Order order);
}

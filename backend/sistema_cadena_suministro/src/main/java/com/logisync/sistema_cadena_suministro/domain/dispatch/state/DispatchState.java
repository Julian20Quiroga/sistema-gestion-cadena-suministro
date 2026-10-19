package com.logisync.sistema_cadena_suministro.domain.dispatch.state;

import com.logisync.sistema_cadena_suministro.domain.enums.DispatchStatus;
import com.logisync.sistema_cadena_suministro.domain.models.Dispatch;

/**
 * Interfaz de estado para el ciclo de vida del despacho de mercancía.
 * Patrón aplicado: State (controla las transiciones del despacho:
 * PENDING -> PACKED -> IN_TRANSIT -> DELIVERED o FAILED).
 */
public interface DispatchState {

    /**
     * Retorna el enumerado correspondiente al estado del despacho.
     */
    DispatchStatus getStatus();

    /**
     * Avanza el despacho a la fase de preparación y empaque en bodega.
     */
    void startPreparation(Dispatch dispatch);

    /**
     * Avanza el despacho a la fase de transporte en ruta vehicular.
     */
    void startTransport(Dispatch dispatch);

    /**
     * Confirma la entrega satisfactoria del despacho.
     */
    void markDelivered(Dispatch dispatch);

    /**
     * Cancela o declara fallido el despacho.
     */
    void cancel(Dispatch dispatch);
}

package com.logisync.sistema_cadena_suministro.domain.procurement.state;

import com.logisync.sistema_cadena_suministro.domain.enums.PurchaseOrderStatus;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrder;

/**
 * Interfaz de estado para órdenes de compra a proveedores.
 * Patrón aplicado: State (encapsula el comportamiento y las transiciones válidas según el estado
 * del ciclo de vida de la orden de compra: DRAFT -> PENDING_APPROVAL -> APPROVED -> ORDERED -> RECEIVED o CANCELLED).
 */
public interface PurchaseOrderState {

    /**
     * Retorna el identificador del estado correspondiente.
     */
    PurchaseOrderStatus getStatus();

    /**
     * Procesa la recepción física de mercancía asociada a la orden de compra.
     * Patrón aplicado: State (comportamiento específico del estado actual).
     */
    void receive(PurchaseOrder order, int totalReceivedSoFar, int totalOrdered);

    /**
     * Ejecuta la cancelación de la orden de compra si el estado actual lo permite.
     */
    void cancel(PurchaseOrder order);
}

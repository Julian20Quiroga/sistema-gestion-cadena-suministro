package com.logisync.sistema_cadena_suministro.infrastructure.mediator;

import com.logisync.sistema_cadena_suministro.application.mediator.Colleague;
import com.logisync.sistema_cadena_suministro.application.mediator.ProcurementColleague;
import com.logisync.sistema_cadena_suministro.application.mediator.SupplyChainMediator;
import com.logisync.sistema_cadena_suministro.domain.models.Order;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mediador concreto que centraliza la comunicación y coordinación de eventos entre subsistemas independientes.
 * Patrón aplicado: Mediator (desacopla Inventario, Órdenes, Aprovisionamiento y Notificaciones,
 * evitando dependencias directas cruzadas punto a punto).
 */
@Component
public class DefaultSupplyChainMediator implements SupplyChainMediator {

    private final List<Colleague> colleagues = new ArrayList<>();
    private ProcurementColleague procurementColleague;

    /**
     * Registra un nuevo colega en el mediador para que participe en el intercambio de eventos.
     */
    @Override
    public void registerColleague(Colleague colleague) {
        colleagues.add(colleague);
        if (colleague instanceof ProcurementColleague pc) {
            this.procurementColleague = pc;
        }
    }

    /**
     * Notifica a los colegas interesados cuando el stock de un producto desciende del punto de reorden.
     * Patrón aplicado: Mediator (enruta el evento de inventario hacia el módulo de aprovisionamiento).
     */
    @Override
    public void notifyStockLow(Integer productId, long availableStock, int reorderPoint) {
        if (procurementColleague != null) {
            procurementColleague.handleStockLow(productId, availableStock, reorderPoint);
        }
    }

    /**
     * Notifica la emisión de una orden de compra para sincronizar los módulos receptores.
     */
    @Override
    public void notifyPurchaseOrderCreated(PurchaseOrder order) {
        if (procurementColleague != null) {
            procurementColleague.handlePurchaseOrderCreated(order);
        }
    }

    /**
     * Notifica la creación de un nuevo pedido de venta de cliente.
     */
    @Override
    public void notifyOrderCreated(Order order) {
        // Enrutamiento desacoplado para despacho y reserva
    }

    /**
     * Notifica la cancelación de un pedido de venta para disparar la liberación de inventario.
     */
    @Override
    public void notifyOrderCancelled(Order order) {
        // Enrutamiento desacoplado para liberación de stock en bodega
    }
}

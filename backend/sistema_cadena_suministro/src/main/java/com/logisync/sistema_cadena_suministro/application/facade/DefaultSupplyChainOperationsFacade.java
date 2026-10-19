package com.logisync.sistema_cadena_suministro.application.facade;

import com.logisync.sistema_cadena_suministro.application.commands.CreateOrderCommand;
import com.logisync.sistema_cadena_suministro.application.commands.CreatePurchaseOrderCommand;
import com.logisync.sistema_cadena_suministro.application.commands.ReceivePurchaseOrderCommand;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreateOrderUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreatePurchaseOrderUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.ReceivePurchaseOrderUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.InventoryRepository;
import com.logisync.sistema_cadena_suministro.application.responses.OrderAppResponse;
import com.logisync.sistema_cadena_suministro.application.responses.PurchaseOrderAppResponse;
import com.logisync.sistema_cadena_suministro.domain.inventory.StockTotals;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Fachada principal de operaciones de la cadena de suministro.
 * Patrón aplicado: Facade (proporciona una interfaz unificada y de alto nivel que oculta la complejidad
 * de coordinar pedidos de venta, compras a proveedores, recepción física y consulta consolidada de stock).
 */
@Component
@RequiredArgsConstructor
public class DefaultSupplyChainOperationsFacade implements SupplyChainOperationsFacade {

    private final CreateOrderUseCase createOrderUseCase;
    private final CreatePurchaseOrderUseCase createPurchaseOrderUseCase;
    private final ReceivePurchaseOrderUseCase receivePurchaseOrderUseCase;
    private final InventoryRepository inventoryRepository;

    /**
     * Registra un nuevo pedido de cliente a través de una llamada unificada.
     * Patrón aplicado: Facade (delega la creación y validación encadenada al caso de uso de órdenes).
     */
    @Override
    @Transactional
    public OrderAppResponse placeCustomerOrder(CreateOrderCommand command) {
        return createOrderUseCase.create(command);
    }

    /**
     * Orquesta el flujo completo de aprovisionamiento: crea la orden de compra y recibe la mercancía en bodega en un solo paso.
     * Patrón aplicado: Facade (simplifica múltiples pasos transaccionales complejos en un único método de entrada).
     */
    @Override
    @Transactional
    public PurchaseOrderAppResponse procureAndReceiveMerchandise(CreatePurchaseOrderCommand command, Integer storageSpaceId) {
        PurchaseOrderAppResponse created = createPurchaseOrderUseCase.create(command);

        List<ReceivePurchaseOrderCommand.ReceptionDetail> receptionDetails = command.items().stream()
                .map(item -> new ReceivePurchaseOrderCommand.ReceptionDetail(item.productId(), storageSpaceId, item.quantity()))
                .toList();

        return receivePurchaseOrderUseCase.receive(new ReceivePurchaseOrderCommand(created.id(), receptionDetails));
    }

    /**
     * Consulta el balance consolidado de inventario (físico, reservado y disponible) para un producto.
     */
    @Override
    @Transactional(readOnly = true)
    public StockTotals getConsolidatedStock(Integer productId) {
        return inventoryRepository.totalsByProduct(productId);
    }
}

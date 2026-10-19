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
 * Facade: Concrete facade orchestrating operations across multiple subsystems.
 */
@Component
@RequiredArgsConstructor
public class DefaultSupplyChainOperationsFacade implements SupplyChainOperationsFacade {

    private final CreateOrderUseCase createOrderUseCase;
    private final CreatePurchaseOrderUseCase createPurchaseOrderUseCase;
    private final ReceivePurchaseOrderUseCase receivePurchaseOrderUseCase;
    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public OrderAppResponse placeCustomerOrder(CreateOrderCommand command) {
        return createOrderUseCase.create(command);
    }

    @Override
    @Transactional
    public PurchaseOrderAppResponse procureAndReceiveMerchandise(CreatePurchaseOrderCommand command, Integer storageSpaceId) {
        PurchaseOrderAppResponse created = createPurchaseOrderUseCase.create(command);

        List<ReceivePurchaseOrderCommand.ReceptionDetail> receptionDetails = command.items().stream()
                .map(item -> new ReceivePurchaseOrderCommand.ReceptionDetail(item.productId(), storageSpaceId, item.quantity()))
                .toList();

        return receivePurchaseOrderUseCase.receive(new ReceivePurchaseOrderCommand(created.id(), receptionDetails));
    }

    @Override
    @Transactional(readOnly = true)
    public StockTotals getConsolidatedStock(Integer productId) {
        return inventoryRepository.totalsByProduct(productId);
    }
}

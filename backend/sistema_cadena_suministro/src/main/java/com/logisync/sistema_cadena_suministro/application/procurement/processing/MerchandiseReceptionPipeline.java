package com.logisync.sistema_cadena_suministro.application.procurement.processing;

import com.logisync.sistema_cadena_suministro.application.commands.ReceivePurchaseOrderCommand;
import com.logisync.sistema_cadena_suministro.application.commands.RegisterEntryCommand;
import com.logisync.sistema_cadena_suministro.application.ports.driven.RegisterEntryUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.PurchaseOrderRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.StorageSpaceRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.StoredItemsReader;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrder;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrderDetail;
import com.logisync.sistema_cadena_suministro.domain.models.StorageSpace;
import com.logisync.sistema_cadena_suministro.domain.procurement.chain.ReceptionItem;
import com.logisync.sistema_cadena_suministro.domain.procurement.chain.ReceptionLocationCapacityValidator;
import com.logisync.sistema_cadena_suministro.domain.procurement.chain.ReceptionOrderStateValidator;
import com.logisync.sistema_cadena_suministro.domain.procurement.chain.ReceptionQuantityValidator;
import com.logisync.sistema_cadena_suministro.domain.procurement.chain.ReceptionValidationContext;
import com.logisync.sistema_cadena_suministro.domain.procurement.chain.ReceptionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Template Method: Pipeline for receiving purchased merchandise into warehouse inventory.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MerchandiseReceptionPipeline {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final StorageSpaceRepository storageSpaceRepository;
    private final StoredItemsReader storedItemsReader;
    private final RegisterEntryUseCase registerEntryUseCase;

    public final PurchaseOrder process(ReceivePurchaseOrderCommand command) {
        PurchaseOrder order = loadOrder(command.purchaseOrderId());
        validate(order, command);
        updateOrderDetails(order, command);
        applyStateTransition(order);
        recordInventoryEntries(order, command);
        PurchaseOrder saved = persist(order);
        audit(saved);
        return saved;
    }

    protected PurchaseOrder loadOrder(Integer orderId) {
        return purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", orderId));
    }

    protected void validate(PurchaseOrder order, ReceivePurchaseOrderCommand command) {
        List<ReceptionItem> items = command.items().stream()
                .map(i -> new ReceptionItem(i.productId(), i.storageSpaceId(), i.quantity()))
                .toList();

        List<Integer> spaceIds = items.stream().map(ReceptionItem::storageSpaceId).distinct().toList();
        Map<Integer, StorageSpace> spaces = new HashMap<>();
        Map<Integer, Long> unitsInSpace = new HashMap<>();
        for (Integer sid : spaceIds) {
            storageSpaceRepository.findById(sid).ifPresent(s -> spaces.put(sid, s));
            unitsInSpace.put(sid, storedItemsReader.unitsInSpace(sid));
        }

        ReceptionValidationContext context = new ReceptionValidationContext(order, items, spaces, unitsInSpace);

        ReceptionValidator validator = new ReceptionOrderStateValidator();
        validator.linkWith(new ReceptionQuantityValidator())
                .linkWith(new ReceptionLocationCapacityValidator());

        validator.validate(context);
    }

    protected void updateOrderDetails(PurchaseOrder order, ReceivePurchaseOrderCommand command) {
        Map<Integer, Integer> receivedPerProduct = command.items().stream()
                .collect(Collectors.groupingBy(ReceivePurchaseOrderCommand.ReceptionDetail::productId,
                        Collectors.summingInt(ReceivePurchaseOrderCommand.ReceptionDetail::quantity)));

        for (PurchaseOrderDetail detail : order.getDetails()) {
            Integer incoming = receivedPerProduct.get(detail.getProductId());
            if (incoming != null && incoming > 0) {
                detail.addReceived(incoming);
            }
        }
    }

    protected void applyStateTransition(PurchaseOrder order) {
        order.applyReception(order.totalReceivedQuantity(), order.totalOrderedQuantity());
    }

    protected void recordInventoryEntries(PurchaseOrder order, ReceivePurchaseOrderCommand command) {
        for (var item : command.items()) {
            registerEntryUseCase.registerEntry(new RegisterEntryCommand(
                    item.storageSpaceId(),
                    item.productId(),
                    item.quantity(),
                    "PO-RECEPTION-" + order.getId()
            ));
        }
    }

    protected PurchaseOrder persist(PurchaseOrder order) {
        return purchaseOrderRepository.save(order);
    }

    protected void audit(PurchaseOrder order) {
        log.info("Purchase order {} received. New status: {}", order.getId(), order.getStatus());
    }
}

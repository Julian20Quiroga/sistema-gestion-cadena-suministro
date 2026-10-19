package com.logisync.sistema_cadena_suministro.application.service;

import com.logisync.sistema_cadena_suministro.application.commands.CreatePurchaseOrderCommand;
import com.logisync.sistema_cadena_suministro.application.commands.ReceivePurchaseOrderCommand;
import com.logisync.sistema_cadena_suministro.application.mappers.PurchaseOrderApplicationMapper;
import com.logisync.sistema_cadena_suministro.application.mediator.SupplyChainMediator;
import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CancelPurchaseOrderUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.CreatePurchaseOrderUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryPurchaseOrderUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.ReceivePurchaseOrderUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.ProductRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.PurchaseOrderRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.SupplierRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.WarehouseRepository;
import com.logisync.sistema_cadena_suministro.application.procurement.processing.MerchandiseReceptionPipeline;
import com.logisync.sistema_cadena_suministro.application.responses.PurchaseOrderAppResponse;
import com.logisync.sistema_cadena_suministro.domain.enums.PurchaseOrderStatus;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.models.Product;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrder;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrderDetail;
import com.logisync.sistema_cadena_suministro.domain.models.Supplier;
import com.logisync.sistema_cadena_suministro.domain.models.Warehouse;
import com.logisync.sistema_cadena_suministro.domain.procurement.chain.CreationOrderValidator;
import com.logisync.sistema_cadena_suministro.domain.procurement.chain.CreationValidationContext;
import com.logisync.sistema_cadena_suministro.domain.procurement.chain.ProductBelongsToSupplierValidator;
import com.logisync.sistema_cadena_suministro.domain.procurement.chain.SupplierActiveValidator;
import com.logisync.sistema_cadena_suministro.domain.procurement.chain.WarehouseActiveValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService implements CreatePurchaseOrderUseCase, ReceivePurchaseOrderUseCase,
        CancelPurchaseOrderUseCase, QueryPurchaseOrderUseCase {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final MerchandiseReceptionPipeline receptionPipeline;
    private final SupplyChainMediator mediator;
    private final PurchaseOrderApplicationMapper mapper;
    private final Clock clock;

    @Override
    @Transactional
    public PurchaseOrderAppResponse create(CreatePurchaseOrderCommand command) {
        Supplier supplier = supplierRepository.findById(command.supplierId()).orElse(null);
        Warehouse warehouse = warehouseRepository.findById(command.warehouseId()).orElse(null);

        Map<Integer, Product> products = new HashMap<>();
        if (command.items() != null) {
            for (var item : command.items()) {
                productRepository.findById(item.productId()).ifPresent(p -> products.put(p.getId(), p));
            }
        }

        PurchaseOrder order = new PurchaseOrder();
        order.setSupplierId(command.supplierId());
        order.setWarehouseId(command.warehouseId());
        order.setOrderDate(LocalDateTime.now(clock));

        List<PurchaseOrderDetail> details = new ArrayList<>();
        if (command.items() != null) {
            for (var item : command.items()) {
                details.add(new PurchaseOrderDetail(null, item.productId(), null, item.quantity(), 0, item.unitPrice()));
            }
        }
        order.setDetails(details);

        CreationOrderValidator validator = new SupplierActiveValidator();
        validator.linkWith(new WarehouseActiveValidator())
                .linkWith(new ProductBelongsToSupplierValidator());
        validator.validate(new CreationValidationContext(order, supplier, warehouse, products));

        PurchaseOrder saved = purchaseOrderRepository.save(order);
        mediator.notifyPurchaseOrderCreated(saved);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PurchaseOrderAppResponse receive(ReceivePurchaseOrderCommand command) {
        PurchaseOrder received = receptionPipeline.process(command);
        return mapper.toResponse(received);
    }

    @Override
    @Transactional
    public PurchaseOrderAppResponse cancel(Integer purchaseOrderId) {
        PurchaseOrder order = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", purchaseOrderId));
        order.cancel();
        return mapper.toResponse(purchaseOrderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderAppResponse findById(Integer id) {
        return purchaseOrderRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<PurchaseOrderAppResponse> findAll(Integer supplierId, Integer warehouseId,
                                                        PurchaseOrderStatus status, PageQuery pageQuery) {
        return purchaseOrderRepository.findAll(supplierId, warehouseId, status, pageQuery)
                .map(mapper::toResponse);
    }
}

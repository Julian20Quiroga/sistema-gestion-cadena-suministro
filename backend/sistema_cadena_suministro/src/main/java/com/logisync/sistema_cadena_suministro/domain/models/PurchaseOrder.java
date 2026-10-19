package com.logisync.sistema_cadena_suministro.domain.models;

import com.logisync.sistema_cadena_suministro.domain.enums.PurchaseOrderStatus;
import com.logisync.sistema_cadena_suministro.domain.procurement.state.PurchaseOrderState;
import com.logisync.sistema_cadena_suministro.domain.procurement.state.PurchaseOrderStateFactory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PurchaseOrder {
    private Integer id;
    private Integer supplierId;
    private LocalDateTime orderDate;
    private PurchaseOrderStatus status;
    private Integer warehouseId;
    private List<PurchaseOrderDetail> details = new ArrayList<>();

    private PurchaseOrderState state;

    public PurchaseOrder() {
        changeState(new com.logisync.sistema_cadena_suministro.domain.procurement.state.RegisteredState());
    }

    public PurchaseOrder(Integer id, Integer supplierId, LocalDateTime orderDate, PurchaseOrderStatus status,
                         Integer warehouseId, List<PurchaseOrderDetail> details) {
        this.id = id;
        this.supplierId = supplierId;
        this.orderDate = orderDate;
        this.warehouseId = warehouseId;
        this.details = details != null ? details : new ArrayList<>();
        changeState(PurchaseOrderStateFactory.from(status));
    }

    public void changeState(PurchaseOrderState newState) {
        this.state = newState;
        this.status = newState.getStatus();
    }

    public void applyReception(int totalReceivedSoFar, int totalOrdered) {
        this.state.receive(this, totalReceivedSoFar, totalOrdered);
    }

    public void cancel() {
        this.state.cancel(this);
    }

    public int totalOrderedQuantity() {
        return details.stream().mapToInt(PurchaseOrderDetail::getQuantity).sum();
    }

    public int totalReceivedQuantity() {
        return details.stream().mapToInt(PurchaseOrderDetail::getReceivedQuantity).sum();
    }
}

package com.logisync.sistema_cadena_suministro.domain.models;

import com.logisync.sistema_cadena_suministro.domain.dispatch.state.DispatchState;
import com.logisync.sistema_cadena_suministro.domain.dispatch.state.DispatchStateFactory;
import com.logisync.sistema_cadena_suministro.domain.dispatch.state.PendingDispatchState;
import com.logisync.sistema_cadena_suministro.domain.enums.DispatchStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Dispatch {
    private Integer id;
    private Integer orderId;
    private Integer warehouseId;
    private Integer vehicleId;
    private DispatchStatus status;
    private List<DetailDispatch> details = new ArrayList<>();

    private DispatchState state;

    public Dispatch() {
        changeState(new PendingDispatchState());
    }

    public Dispatch(Integer id, Integer orderId, Integer warehouseId, Integer vehicleId,
                    DispatchStatus status, List<DetailDispatch> details) {
        this.id = id;
        this.orderId = orderId;
        this.warehouseId = warehouseId;
        this.vehicleId = vehicleId;
        this.details = details != null ? details : new ArrayList<>();
        changeState(DispatchStateFactory.from(status));
    }

    public void setStatus(DispatchStatus status) {
        this.status = status;
        this.state = DispatchStateFactory.from(status);
    }

    public void changeState(DispatchState newState) {
        this.state = newState;
        this.status = newState.getStatus();
    }

    public void startPreparation() {
        this.state.startPreparation(this);
    }

    public void startTransport() {
        this.state.startTransport(this);
    }

    public void markDelivered() {
        this.state.markDelivered(this);
    }

    public void cancel() {
        this.state.cancel(this);
    }
}

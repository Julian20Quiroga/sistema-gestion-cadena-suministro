package com.logisync.sistema_cadena_suministro.domain.models;

import com.logisync.sistema_cadena_suministro.domain.enums.SupplyNeedOrigin;
import com.logisync.sistema_cadena_suministro.domain.enums.SupplyNeedStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplyNeed {
    private Integer id;
    private Integer productId;
    private int requestedQuantity;
    private SupplyNeedOrigin origin;
    private SupplyNeedStatus status;
    private Integer purchaseOrderId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void cover(Integer purchaseOrderId) {
        this.status = SupplyNeedStatus.COVERED;
        this.purchaseOrderId = purchaseOrderId;
    }

    public void dismiss() {
        this.status = SupplyNeedStatus.DISMISSED;
    }
}

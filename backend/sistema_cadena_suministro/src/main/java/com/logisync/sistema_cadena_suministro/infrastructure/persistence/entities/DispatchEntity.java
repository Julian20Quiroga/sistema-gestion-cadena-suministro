package com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities;

import com.logisync.sistema_cadena_suministro.domain.enums.DispatchStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dispatches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispatchEntity extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @Column(name = "warehouses_id", nullable = false)
    private Integer warehouseId;

    @Column(name = "vehicle_id")
    private Integer vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DispatchStatus status;

    @OneToMany(mappedBy = "dispatch", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DetailDispatchEntity> details = new ArrayList<>();
}

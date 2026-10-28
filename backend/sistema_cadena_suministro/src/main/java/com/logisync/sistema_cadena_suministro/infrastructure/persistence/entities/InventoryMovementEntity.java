package com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities;

import com.logisync.sistema_cadena_suministro.domain.inventory.MovementStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventory_movements")
@Getter
@Setter
public class InventoryMovementEntity extends BaseEntity {

    @Column(name = "movement_type")
    private String movementType;

    @Column(name = "movement_date")
    private LocalDateTime movementDate;

    @Column(name = "reason")
    private String reason;

    @Column(name = "reference")
    private String reference;

    @Column(name = "movement_status")
    @Enumerated(EnumType.STRING)
    private MovementStatus status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "inventory_movement_id", nullable = false)
    private List<MovementDetailEntity> details = new ArrayList<>();
}

package com.logisync.sistema_cadena_suministro.application.inventory.processing;

import com.logisync.sistema_cadena_suministro.application.inventory.LowStockMonitor;
import com.logisync.sistema_cadena_suministro.application.ports.driving.DomainEventPublisher;
import com.logisync.sistema_cadena_suministro.application.ports.driving.InventoryRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.MovementRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;

/** Shared collaborators of every movement processor. */
@Getter
@Component
@RequiredArgsConstructor
public class MovementContext {
    private final InventoryRepository inventoryRepository;
    private final MovementRepository movementRepository;
    private final DomainEventPublisher eventPublisher;
    private final LowStockMonitor lowStockMonitor;
    private final Clock clock;
}

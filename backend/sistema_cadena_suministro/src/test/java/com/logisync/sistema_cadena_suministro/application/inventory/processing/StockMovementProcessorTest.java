package com.logisync.sistema_cadena_suministro.application.inventory.processing;

import com.logisync.sistema_cadena_suministro.application.commands.RegisterExitCommand;
import com.logisync.sistema_cadena_suministro.application.events.StockMovementRecordedEvent;
import com.logisync.sistema_cadena_suministro.application.inventory.LowStockMonitor;
import com.logisync.sistema_cadena_suministro.application.ports.driving.DomainEventPublisher;
import com.logisync.sistema_cadena_suministro.application.ports.driving.InventoryRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.MovementRepository;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryMovement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockMovementProcessorTest {

    @Mock
    InventoryRepository inventoryRepository;
    @Mock
    MovementRepository movementRepository;
    @Mock
    DomainEventPublisher eventPublisher;
    @Mock
    LowStockMonitor lowStockMonitor;

    ExitMovementProcessor processor;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        MovementContext context = new MovementContext(inventoryRepository, movementRepository, eventPublisher,
                lowStockMonitor, clock);
        processor = new ExitMovementProcessor(context);
    }

    @Test
    void runsValidateApplyPersistAndNotifyInOrder() {
        InventoryItem item = new InventoryItem(1, 10, 100, 10, 0, 0, 1);
        when(inventoryRepository.findBySpaceAndProduct(100, 10)).thenReturn(Optional.of(item));
        when(movementRepository.save(any())).thenAnswer(inv -> {
            InventoryMovement movement = inv.getArgument(0);
            movement.setId(55);
            return movement;
        });

        InventoryMovement movement = processor.process(new RegisterExitCommand(100, 10, 4, "REF-1"));

        assertEquals(6, item.getPhysicalQuantity());
        assertEquals(55, movement.getId());
        InOrder order = inOrder(inventoryRepository, movementRepository, eventPublisher, lowStockMonitor);
        order.verify(inventoryRepository).save(item);
        order.verify(movementRepository).save(any());
        order.verify(eventPublisher).publish(any(StockMovementRecordedEvent.class));
        order.verify(lowStockMonitor).check(10);
    }

    @Test
    void failedValidationStopsTheFlowBeforeAnyWrite() {
        InventoryItem item = new InventoryItem(1, 10, 100, 2, 0, 0, 1);
        when(inventoryRepository.findBySpaceAndProduct(100, 10)).thenReturn(Optional.of(item));

        assertThrows(BusinessRuleException.class,
                () -> processor.process(new RegisterExitCommand(100, 10, 5, null)));

        verify(inventoryRepository, never()).save(any());
        verifyNoInteractions(movementRepository, eventPublisher, lowStockMonitor);
    }
}

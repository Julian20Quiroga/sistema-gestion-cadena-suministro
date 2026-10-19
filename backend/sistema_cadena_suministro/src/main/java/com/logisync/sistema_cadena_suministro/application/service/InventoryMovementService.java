package com.logisync.sistema_cadena_suministro.application.service;

import com.logisync.sistema_cadena_suministro.application.commands.ConsumeReservationCommand;
import com.logisync.sistema_cadena_suministro.application.commands.RegisterAdjustmentCommand;
import com.logisync.sistema_cadena_suministro.application.commands.RegisterEntryCommand;
import com.logisync.sistema_cadena_suministro.application.commands.RegisterExitCommand;
import com.logisync.sistema_cadena_suministro.application.commands.RegisterTransferCommand;
import com.logisync.sistema_cadena_suministro.application.events.StockMovementRecordedEvent;
import com.logisync.sistema_cadena_suministro.application.inventory.LowStockMonitor;
import com.logisync.sistema_cadena_suministro.application.inventory.processing.StockMovementProcessor;
import com.logisync.sistema_cadena_suministro.application.mappers.InventoryApplicationMapper;
import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import com.logisync.sistema_cadena_suministro.application.ports.driven.ConsumeReservedStockUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.QueryMovementUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.RegisterAdjustmentUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.RegisterEntryUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.RegisterExitUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.RegisterTransferUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driven.UndoMovementUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.DomainEventPublisher;
import com.logisync.sistema_cadena_suministro.application.ports.driving.InventoryRepository;
import com.logisync.sistema_cadena_suministro.application.ports.driving.MovementRepository;
import com.logisync.sistema_cadena_suministro.application.queries.MovementFilter;
import com.logisync.sistema_cadena_suministro.application.responses.MovementAppResponse;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ConflictException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryMovement;
import com.logisync.sistema_cadena_suministro.domain.inventory.MovementDetail;
import com.logisync.sistema_cadena_suministro.domain.inventory.MovementStatus;
import com.logisync.sistema_cadena_suministro.domain.inventory.command.RecordedMovementCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class InventoryMovementService implements RegisterEntryUseCase, RegisterExitUseCase,
        RegisterAdjustmentUseCase, RegisterTransferUseCase, ConsumeReservedStockUseCase, UndoMovementUseCase,
        QueryMovementUseCase {

    private final InventoryApplicationMapper mapper;
    private final StockMovementProcessor<RegisterEntryCommand> entryProcessor;
    private final StockMovementProcessor<RegisterExitCommand> exitProcessor;
    private final StockMovementProcessor<RegisterAdjustmentCommand> adjustmentProcessor;
    private final StockMovementProcessor<RegisterTransferCommand> transferProcessor;
    private final StockMovementProcessor<ConsumeReservationCommand> dispatchExitProcessor;
    private final InventoryRepository inventoryRepository;
    private final MovementRepository movementRepository;
    private final DomainEventPublisher eventPublisher;
    private final LowStockMonitor lowStockMonitor;

    public InventoryMovementService(InventoryApplicationMapper mapper,
            StockMovementProcessor<RegisterEntryCommand> entryProcessor,
            StockMovementProcessor<RegisterExitCommand> exitProcessor,
            StockMovementProcessor<RegisterAdjustmentCommand> adjustmentProcessor,
            StockMovementProcessor<RegisterTransferCommand> transferProcessor,
            StockMovementProcessor<ConsumeReservationCommand> dispatchExitProcessor,
            InventoryRepository inventoryRepository, MovementRepository movementRepository,
            DomainEventPublisher eventPublisher, LowStockMonitor lowStockMonitor) {
        this.mapper = mapper;
        this.entryProcessor = entryProcessor;
        this.exitProcessor = exitProcessor;
        this.adjustmentProcessor = adjustmentProcessor;
        this.transferProcessor = transferProcessor;
        this.dispatchExitProcessor = dispatchExitProcessor;
        this.inventoryRepository = inventoryRepository;
        this.movementRepository = movementRepository;
        this.eventPublisher = eventPublisher;
        this.lowStockMonitor = lowStockMonitor;
    }

    @Override
    @Transactional
    public MovementAppResponse registerEntry(RegisterEntryCommand command) {
        return mapper.toResponse(entryProcessor.process(command));
    }

    @Override
    @Transactional
    public MovementAppResponse registerExit(RegisterExitCommand command) {
        return mapper.toResponse(exitProcessor.process(command));
    }

    @Override
    @Transactional
    public MovementAppResponse registerAdjustment(RegisterAdjustmentCommand command) {
        return mapper.toResponse(adjustmentProcessor.process(command));
    }

    @Override
    @Transactional
    public MovementAppResponse registerTransfer(RegisterTransferCommand command) {
        return mapper.toResponse(transferProcessor.process(command));
    }

    @Override
    @Transactional
    public MovementAppResponse consume(ConsumeReservationCommand command) {
        return mapper.toResponse(dispatchExitProcessor.process(command));
    }

    @Override
    @Transactional
    public MovementAppResponse undo(Integer movementId) {
        InventoryMovement movement = movementRepository.findById(movementId)
                .orElseThrow(() -> new ResourceNotFoundException("Movement", movementId));
        if (movement.getStatus() == MovementStatus.REVERSED) {
            throw new ConflictException("Movement " + movementId + " was already reversed");
        }
        Map<Integer, InventoryItem> items = inventoryRepository
                .findByIds(movement.getDetails().stream().map(MovementDetail::inventoryId).toList()).stream()
                .collect(Collectors.toMap(InventoryItem::getId, Function.identity()));
        RecordedMovementCommand command = new RecordedMovementCommand(movement, items);
        command.undo();
        command.items().forEach(inventoryRepository::save);
        movement.markReversed();
        InventoryMovement saved = movementRepository.save(movement);
        eventPublisher.publish(new StockMovementRecordedEvent(saved.getId(), saved.getType().code(),
                "UNDO-" + saved.getId(), List.of()));
        command.items().stream().map(InventoryItem::getProductId).distinct().forEach(lowStockMonitor::check);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<MovementAppResponse> findMovements(MovementFilter filter, PageQuery pageQuery) {
        return movementRepository.search(filter, pageQuery).map(mapper::toResponse);
    }
}

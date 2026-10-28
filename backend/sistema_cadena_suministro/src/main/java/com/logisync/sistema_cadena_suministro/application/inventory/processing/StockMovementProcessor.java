package com.logisync.sistema_cadena_suministro.application.inventory.processing;

import com.logisync.sistema_cadena_suministro.application.events.StockMovementRecordedEvent;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryMovement;
import com.logisync.sistema_cadena_suministro.domain.inventory.command.StockMovementCommand;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * Procesador abstracto para el flujo invariable de movimientos de inventario.
 * Patrón aplicado: Template Method (define el esqueleto invariable del algoritmo en process():
 * validar -> construir comando -> ejecutar -> persistir -> auditar -> notificar observadores;
 * delegando los pasos específicos de validación y armado del comando a las subclases).
 */
@Slf4j
public abstract class StockMovementProcessor<R> {

    protected final MovementContext context;

    protected StockMovementProcessor(MovementContext context) {
        this.context = context;
    }

    /**
     * Método plantilla invariable que ejecuta las fases secuenciales de un movimiento de stock.
     * Patrón aplicado: Template Method (método 'final' que garantiza el orden de ejecución).
     */
    public final InventoryMovement process(R request) {
        validate(request);
        StockMovementCommand command = buildCommand(request);
        command.execute();
        InventoryMovement movement = persist(command, request);
        audit(movement);
        notifyObservers(movement, command);
        return movement;
    }

    /**
     * Paso abstracto: Valida las precondiciones específicas del tipo de movimiento.
     */
    protected abstract void validate(R request);

    /**
     * Paso abstracto: Construye el comando de variación de stock correspondiente (Patrón Command).
     */
    protected abstract StockMovementCommand buildCommand(R request);

    /**
     * Paso abstracto: Extrae el motivo o razón del movimiento.
     */
    protected abstract String reasonOf(R request);

    /**
     * Paso abstracto: Extrae la referencia documental del movimiento.
     */
    protected abstract String referenceOf(R request);

    /**
     * Registra en los logs de auditoría el movimiento procesado.
     */
    protected void audit(InventoryMovement movement) {
        log.info("Inventory movement {} type={} reference={}", movement.getId(), movement.getType(),
                movement.getReference());
    }

    /**
     * Dispara notificaciones a los observadores suscritos ante cambios de stock.
     * Patrón aplicado: Observer (notifica al monitor de stock mínimo por cada producto afectado).
     */
    protected void notifyObservers(InventoryMovement movement, StockMovementCommand command) {
        publishRecorded(movement);
        command.items().stream().map(InventoryItem::getProductId).distinct()
                .forEach(context.getLowStockMonitor()::check);
    }

    /**
     * Publica el evento de dominio de movimiento registrado.
     */
    protected final void publishRecorded(InventoryMovement movement) {
        context.getEventPublisher().publish(new StockMovementRecordedEvent(movement.getId(),
                movement.getType().code(), movement.getReference(),
                movement.getDetails().stream().map(d -> new StockMovementRecordedEvent.ProductQuantity(
                        d.productId(), d.storageSpaceId(), d.quantity())).toList()));
    }

    /**
     * Persiste los balances de stock modificados y el registro histórico del movimiento.
     */
    private InventoryMovement persist(StockMovementCommand command, R request) {
        command.items().forEach(context.getInventoryRepository()::save);
        InventoryMovement movement = InventoryMovement.from(command, LocalDateTime.now(context.getClock()),
                reasonOf(request), referenceOf(request));
        return context.getMovementRepository().save(movement);
    }
}

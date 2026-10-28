package com.logisync.sistema_cadena_suministro.domain.inventory.command;

import com.logisync.sistema_cadena_suministro.domain.inventory.InventoryItem;
import com.logisync.sistema_cadena_suministro.domain.inventory.MovementType;

import java.util.List;

/**
 * Clase base para comandos de variación de stock.
 * Patrón aplicado: Command (encapsula las operaciones de bodega como objetos ejecutables y reversibles,
 * permitiendo trazabilidad, desacoplamiento y compensación mediante undo()).
 */
public abstract class StockMovementCommand {

    private final MovementType type;
    private final List<ItemDelta> deltas;

    protected StockMovementCommand(MovementType type, List<ItemDelta> deltas) {
        this.type = type;
        this.deltas = List.copyOf(deltas);
    }

    /**
     * Ejecuta el comando aplicando las variaciones físicas y reservadas sobre cada ítem.
     * Patrón aplicado: Command (ejecuta la acción encapsulada).
     */
    public void execute() {
        deltas.forEach(d -> d.item().apply(d.physical(), d.reserved()));
    }

    /**
     * Revierte el comando aplicando los deltas inversos sobre los ítems afectados.
     * Patrón aplicado: Command (operación de compensación / rollback).
     */
    public void undo() {
        for (int i = deltas.size() - 1; i >= 0; i--) {
            ItemDelta d = deltas.get(i);
            d.item().apply(-d.physical(), -d.reserved());
        }
    }

    /**
     * Retorna el tipo de movimiento de inventario asociado al comando.
     */
    public MovementType type() {
        return type;
    }

    /**
     * Retorna la lista inmutable de deltas a aplicar.
     */
    public List<ItemDelta> deltas() {
        return deltas;
    }

    /**
     * Retorna los ítems de inventario únicos afectados por el comando.
     */
    public List<InventoryItem> items() {
        return deltas.stream().map(ItemDelta::item).distinct().toList();
    }
}

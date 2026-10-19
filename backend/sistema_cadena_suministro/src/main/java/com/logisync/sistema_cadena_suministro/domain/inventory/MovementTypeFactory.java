package com.logisync.sistema_cadena_suministro.domain.inventory;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fábrica Flyweight para descriptores de tipos de movimiento de inventario.
 * Patrón aplicado: Flyweight (reutiliza y comparte una única instancia inmutable de MovementType
 * por cada código ENTRADA, SALIDA, AJUSTE, TRASLADO mediante una caché en memoria concurrente,
 * ahorrando memoria ante miles de transacciones de bodega).
 */
public final class MovementTypeFactory {

    private record Definition(String label, boolean requiresReason) {
    }

    private static final Map<String, Definition> DEFINITIONS = Map.of(
            "ENTRADA", new Definition("Entry", false),
            "SALIDA", new Definition("Exit", false),
            "AJUSTE", new Definition("Adjustment", true),
            "TRASLADO", new Definition("Transfer", false));

    private static final Map<String, MovementType> CACHE = new ConcurrentHashMap<>();

    public static final MovementType ENTRY = of("ENTRADA");
    public static final MovementType EXIT = of("SALIDA");
    public static final MovementType ADJUSTMENT = of("AJUSTE");
    public static final MovementType TRANSFER = of("TRASLADO");

    private MovementTypeFactory() {
    }

    /**
     * Retorna la instancia compartida (Flyweight) correspondiente al código solicitado.
     * Patrón aplicado: Flyweight (búsqueda y retorno desde caché concurrente).
     */
    public static MovementType of(String code) {
        String key = code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
        return CACHE.computeIfAbsent(key, MovementTypeFactory::create);
    }

    /**
     * Retorna el número de instancias flyweight cacheadas en memoria.
     */
    public static int cachedInstances() {
        return CACHE.size();
    }

    /**
     * Instancia de forma perezosa el descriptor del tipo de movimiento.
     */
    private static MovementType create(String code) {
        Definition definition = DEFINITIONS.get(code);
        if (definition == null) {
            throw new IllegalArgumentException("Unknown movement type: " + code);
        }
        return new MovementType(code, definition.label(), definition.requiresReason());
    }
}

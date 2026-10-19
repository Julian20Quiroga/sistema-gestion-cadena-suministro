package com.logisync.sistema_cadena_suministro.domain.inventory;

/** Flyweight: immutable, shared descriptor of an inventory movement type (intrinsic state only). */
public final class MovementType {

    private final String code;
    private final String label;
    private final boolean requiresReason;

    MovementType(String code, String label, boolean requiresReason) {
        this.code = code;
        this.label = label;
        this.requiresReason = requiresReason;
    }

    public String code() {
        return code;
    }

    public String label() {
        return label;
    }

    public boolean requiresReason() {
        return requiresReason;
    }

    @Override
    public String toString() {
        return code;
    }
}

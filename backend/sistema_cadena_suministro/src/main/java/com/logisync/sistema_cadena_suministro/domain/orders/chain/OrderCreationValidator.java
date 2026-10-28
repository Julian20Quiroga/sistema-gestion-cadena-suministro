package com.logisync.sistema_cadena_suministro.domain.orders.chain;

/**
 * Validador abstracto base para la creación de órdenes de venta de clientes.
 * Patrón aplicado: Chain of Responsibility (Manejador abstracto que encadena los pasos de validación:
 * datos de cliente -> productos activos -> disponibilidad de stock).
 */
public abstract class OrderCreationValidator {

    private OrderCreationValidator next;

    /**
     * Enlaza el siguiente validador en la cadena.
     */
    public OrderCreationValidator linkWith(OrderCreationValidator next) {
        this.next = next;
        return next;
    }

    /**
     * Ejecuta la validación propia y propaga al siguiente eslabón si no hay errores.
     * Patrón aplicado: Chain of Responsibility (propagación secuencial).
     */
    public void validate(OrderCreationContext context) {
        check(context);
        if (next != null) {
            next.validate(context);
        }
    }

    /**
     * Método abstracto que contiene la regla de validación específica de este manejador.
     */
    protected abstract void check(OrderCreationContext context);
}

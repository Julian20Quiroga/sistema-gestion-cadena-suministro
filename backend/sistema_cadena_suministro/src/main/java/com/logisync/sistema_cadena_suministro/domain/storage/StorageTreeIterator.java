package com.logisync.sistema_cadena_suministro.domain.storage;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterador para el recorrido en profundidad (Depth-First Search) del árbol de espacios de almacén.
 * Patrón aplicado: Iterator (permite explorar jerarquías compuestas de Zonas, Racks y Bahías de forma perezosa
 * sin acoplarse a la estructura interna del árbol Composite).
 */
public class StorageTreeIterator implements Iterator<StorageNode> {

    private final Deque<StorageNode> pending = new ArrayDeque<>();

    public StorageTreeIterator(StorageNode root) {
        pending.push(root);
    }

    /**
     * Verifica si existen más nodos de almacenamiento pendientes por visitar en la pila.
     */
    @Override
    public boolean hasNext() {
        return !pending.isEmpty();
    }

    /**
     * Retorna el siguiente nodo del árbol de almacenamiento y apila sus hijos en orden inverso.
     */
    @Override
    public StorageNode next() {
        if (pending.isEmpty()) {
            throw new NoSuchElementException("No hay más nodos de almacenamiento disponibles");
        }
        StorageNode node = pending.pop();
        List<StorageNode> children = node.children();
        for (int i = children.size() - 1; i >= 0; i--) {
            pending.push(children.get(i));
        }
        return node;
    }
}

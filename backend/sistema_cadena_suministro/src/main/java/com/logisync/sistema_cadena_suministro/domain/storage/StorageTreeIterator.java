package com.logisync.sistema_cadena_suministro.domain.storage;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/** Iterator: lazy depth-first traversal of the storage tree. */
public class StorageTreeIterator implements Iterator<StorageNode> {

    private final Deque<StorageNode> pending = new ArrayDeque<>();

    public StorageTreeIterator(StorageNode root) {
        pending.push(root);
    }

    @Override
    public boolean hasNext() {
        return !pending.isEmpty();
    }

    @Override
    public StorageNode next() {
        if (pending.isEmpty()) {
            throw new NoSuchElementException();
        }
        StorageNode node = pending.pop();
        List<StorageNode> children = node.children();
        for (int i = children.size() - 1; i >= 0; i--) {
            pending.push(children.get(i));
        }
        return node;
    }
}

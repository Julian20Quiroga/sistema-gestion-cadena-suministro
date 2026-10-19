package com.logisync.sistema_cadena_suministro.domain.storage;

import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;

import java.util.Iterator;
import java.util.List;

/** Composite: component common to containers (warehouse, zone, rack) and leaves (location). */
public interface StorageNode extends Iterable<StorageNode> {

    Integer id();

    String name();

    StorageSpaceType type();

    long capacity();

    long occupancy();

    List<StorageNode> children();

    <R> R accept(StorageNodeVisitor<R> visitor);

    default long freeCapacity() {
        return capacity() - occupancy();
    }

    /** Iterator: depth-first, pre-order traversal without exposing the internal structure. */
    @Override
    default Iterator<StorageNode> iterator() {
        return new StorageTreeIterator(this);
    }
}

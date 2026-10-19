package com.logisync.sistema_cadena_suministro.domain.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Composite: node that aggregates capacity and occupancy recursively from its children. */
public abstract class StorageContainer implements StorageNode {

    private final Integer id;
    private final String name;
    private final List<StorageNode> children = new ArrayList<>();

    protected StorageContainer(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public void add(StorageNode child) {
        children.add(child);
    }

    @Override
    public Integer id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public List<StorageNode> children() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public long capacity() {
        return children.stream().mapToLong(StorageNode::capacity).sum();
    }

    @Override
    public long occupancy() {
        return children.stream().mapToLong(StorageNode::occupancy).sum();
    }
}

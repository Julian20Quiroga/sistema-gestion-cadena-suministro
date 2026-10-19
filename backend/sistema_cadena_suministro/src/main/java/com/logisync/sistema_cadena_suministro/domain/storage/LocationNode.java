package com.logisync.sistema_cadena_suministro.domain.storage;

import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;

import java.util.List;

/** Composite leaf: a concrete storage location holding stock of products. */
public class LocationNode implements StorageNode {

    private final Integer id;
    private final String name;
    private final long capacity;
    private final List<StoredItem> items;

    public LocationNode(Integer id, String name, long capacity, List<StoredItem> items) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.items = List.copyOf(items);
    }

    public List<StoredItem> items() {
        return items;
    }

    public boolean canStore(long units) {
        return freeCapacity() >= units;
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
    public StorageSpaceType type() {
        return StorageSpaceType.LOCATION;
    }

    @Override
    public long capacity() {
        return capacity;
    }

    @Override
    public long occupancy() {
        return items.stream().mapToLong(StoredItem::quantity).sum();
    }

    @Override
    public List<StorageNode> children() {
        return List.of();
    }

    @Override
    public <R> R accept(StorageNodeVisitor<R> visitor) {
        return visitor.visitLocation(this);
    }
}

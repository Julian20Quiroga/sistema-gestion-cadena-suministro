package com.logisync.sistema_cadena_suministro.domain.memento.storage;

import java.time.LocalDateTime;

/**
 * Memento: Stores the state of a warehouse storage layout snapshot.
 */
public final class StorageLayoutMemento {

    private final Integer warehouseId;
    private final String snapshotName;
    private final String layoutDataJson;
    private final LocalDateTime timestamp;

    public StorageLayoutMemento(Integer warehouseId, String snapshotName, String layoutDataJson, LocalDateTime timestamp) {
        this.warehouseId = warehouseId;
        this.snapshotName = snapshotName;
        this.layoutDataJson = layoutDataJson;
        this.timestamp = timestamp;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public String getSnapshotName() {
        return snapshotName;
    }

    public String getLayoutDataJson() {
        return layoutDataJson;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

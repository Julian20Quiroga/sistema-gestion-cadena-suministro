package com.logisync.sistema_cadena_suministro.domain.memento.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logisync.sistema_cadena_suministro.domain.models.StorageSpace;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Memento: Originator that creates snapshots of warehouse storage layouts and restores from them.
 */
public class WarehouseLayoutOriginator {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private Integer warehouseId;
    private List<StorageSpace> spaces;

    public void setState(Integer warehouseId, List<StorageSpace> spaces) {
        this.warehouseId = warehouseId;
        this.spaces = spaces;
    }

    public StorageLayoutMemento saveToMemento(String snapshotName) {
        try {
            String json = objectMapper.writeValueAsString(spaces);
            return new StorageLayoutMemento(warehouseId, snapshotName, json, LocalDateTime.now());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize warehouse layout to JSON", e);
        }
    }

    public List<StorageSpace> restoreFromMemento(StorageLayoutMemento memento) {
        try {
            this.warehouseId = memento.getWarehouseId();
            this.spaces = objectMapper.readValue(
                    memento.getLayoutDataJson(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, StorageSpace.class)
            );
            return this.spaces;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize warehouse layout from JSON", e);
        }
    }
}

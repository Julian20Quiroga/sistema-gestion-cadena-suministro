package com.logisync.sistema_cadena_suministro.domain.memento.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logisync.sistema_cadena_suministro.domain.models.StorageSpace;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Originador del patrón Memento para la distribución física del almacén.
 * Patrón aplicado: Memento (Originador que captura el estado interno de los espacios
 * del almacén en un StorageLayoutMemento serializado y permite restaurar dicho estado ante rollbacks).
 */
public class WarehouseLayoutOriginator {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private Integer warehouseId;
    private List<StorageSpace> spaces;

    /**
     * Establece el estado actual del almacén y sus espacios físicos.
     */
    public void setState(Integer warehouseId, List<StorageSpace> spaces) {
        this.warehouseId = warehouseId;
        this.spaces = spaces;
    }

    /**
     * Crea y retorna un nuevo Memento que encapsula el estado actual serializado en formato JSON.
     * Patrón aplicado: Memento (crea el snapshot inmutable).
     */
    public StorageLayoutMemento saveToMemento(String snapshotName) {
        try {
            String json = objectMapper.writeValueAsString(spaces);
            return new StorageLayoutMemento(warehouseId, snapshotName, json, LocalDateTime.now());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize warehouse layout to JSON", e);
        }
    }

    /**
     * Restaura el estado interno del almacén a partir de un Memento previamente capturado.
     * Patrón aplicado: Memento (reconstruye los objetos de dominio desde los datos del snapshot).
     */
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

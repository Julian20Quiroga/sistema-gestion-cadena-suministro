package com.logisync.sistema_cadena_suministro.domain.storage.prototype;

/**
 * Prototype: Declares cloning method for storage configurations.
 */
public interface StorageSpacePrototype extends Cloneable {
    StorageSpacePrototype cloneSpace(String newName, Integer newCapacity);
}

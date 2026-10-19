package com.logisync.sistema_cadena_suministro.application.commands;

import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;

public record CreateStorageSpaceCommand(String name, StorageSpaceType type, Integer parentId, Integer capacity) {
}

package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request;

import com.logisync.sistema_cadena_suministro.domain.enums.StorageSpaceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateStorageSpaceRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull StorageSpaceType type,
        Integer parentId,
        @Positive Integer capacity) {
}

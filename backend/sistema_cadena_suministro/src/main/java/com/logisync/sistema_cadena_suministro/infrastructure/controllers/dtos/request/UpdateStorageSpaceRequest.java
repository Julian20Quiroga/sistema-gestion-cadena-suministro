package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateStorageSpaceRequest(
        @NotBlank @Size(max = 100) String name,
        @Positive Integer capacity) {
}

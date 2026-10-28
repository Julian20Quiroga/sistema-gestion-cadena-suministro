package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSupplierRequest(
        @NotBlank @Size(max = 10) String nit,
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 12) String phone,
        @NotBlank @Email @Size(max = 30) String email,
        SoftDeleteStatus status) {
}

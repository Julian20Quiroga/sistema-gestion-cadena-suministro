package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;

import java.math.BigDecimal;

public record WarehouseResponse(Integer id, String name, String address, String phone,
        SoftDeleteStatus status, Integer headquarterId, BigDecimal latitude, BigDecimal longitude) {
}

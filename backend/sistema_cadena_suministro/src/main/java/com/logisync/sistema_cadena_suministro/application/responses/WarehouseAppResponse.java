package com.logisync.sistema_cadena_suministro.application.responses;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;

import java.math.BigDecimal;

public record WarehouseAppResponse(Integer id, String name, String address, String phone,
        SoftDeleteStatus status, Integer headquarterId, BigDecimal latitude, BigDecimal longitude) {
}

package com.logisync.sistema_cadena_suministro.application.commands;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;

import java.math.BigDecimal;

public record UpdateWarehouseCommand(String name, String address, String phone, Integer headquarterId,
        BigDecimal latitude, BigDecimal longitude, SoftDeleteStatus status) {
}

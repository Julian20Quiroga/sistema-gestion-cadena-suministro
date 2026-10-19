package com.logisync.sistema_cadena_suministro.application.commands;

import java.math.BigDecimal;

public record CreateWarehouseCommand(String name, String address, String phone, Integer headquarterId,
        BigDecimal latitude, BigDecimal longitude) {
}

package com.logisync.sistema_cadena_suministro.domain.routes;

import java.math.BigDecimal;

public record GeoPoint(String label, BigDecimal latitude, BigDecimal longitude, Integer orderId, BigDecimal weightKg) {
}

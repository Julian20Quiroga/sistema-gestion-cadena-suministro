package com.logisync.sistema_cadena_suministro.domain.routes;

import com.logisync.sistema_cadena_suministro.domain.models.Vehicle;

import java.util.List;

public record RouteOptimizationContext(
        GeoPoint originWarehouse,
        List<GeoPoint> deliveryDestinations,
        Vehicle vehicle
) {}

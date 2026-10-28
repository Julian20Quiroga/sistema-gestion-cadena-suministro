package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

public record FreeLocationResponse(Integer id, String name, long capacity, long occupancy, long freeCapacity) {
}

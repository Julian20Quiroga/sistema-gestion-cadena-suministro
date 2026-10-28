package com.logisync.sistema_cadena_suministro.application.responses;

public record FreeLocationAppResponse(Integer id, String name, long capacity, long occupancy, long freeCapacity) {
}

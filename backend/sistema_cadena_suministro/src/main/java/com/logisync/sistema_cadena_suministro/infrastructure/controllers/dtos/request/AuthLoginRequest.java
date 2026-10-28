package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request;

public record AuthLoginRequest(
    String username,
    String password
) {}
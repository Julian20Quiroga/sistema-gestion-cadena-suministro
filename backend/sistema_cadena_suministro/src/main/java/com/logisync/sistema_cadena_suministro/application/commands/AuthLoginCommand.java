package com.logisync.sistema_cadena_suministro.application.commands;

public record AuthLoginCommand(
    String username,
    String password
) {}
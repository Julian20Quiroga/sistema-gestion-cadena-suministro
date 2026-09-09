package com.logisync.sistema_cadena_suministro.application.ports.driving;

public interface AuthenticationPort {
    String authenticate(String username, String password);
}

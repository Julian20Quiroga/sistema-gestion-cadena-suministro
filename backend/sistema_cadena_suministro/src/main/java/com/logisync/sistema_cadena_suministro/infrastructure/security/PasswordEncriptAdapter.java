package com.logisync.sistema_cadena_suministro.infrastructure.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.logisync.sistema_cadena_suministro.application.ports.driving.PasswordEncript;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor  
public class PasswordEncriptAdapter implements PasswordEncript{

    private final PasswordEncoder passwordEncoder;

    @Override
    public String passwordEncript(String password) {
        return passwordEncoder.encode(password);
    }
    
}

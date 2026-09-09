package com.logisync.sistema_cadena_suministro.infrastructure.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.logisync.sistema_cadena_suministro.application.ports.driving.AuthenticationPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthAdapter implements AuthenticationPort {

    private final AuthenticationManager manager;
    private final JwtService jwtService;

    @SuppressWarnings("null")
    @Override
    public String authenticate(String username, String password) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        Authentication userAuthenticated = manager.authenticate(authentication);

        String role = userAuthenticated.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_NOT_FOUND");

        return jwtService.generateToken(userAuthenticated.getName(), role);
    }

}

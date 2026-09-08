package com.logisync.sistema_cadena_suministro.infrastructure.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.AuthLoginRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.AuthLoginResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping("/api/auth")
public class AuthController {
    
    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponse> postMethodName(@RequestBody AuthLoginRequest request) {
        return ResponseEntity.ok().body(null);
    }
    

}

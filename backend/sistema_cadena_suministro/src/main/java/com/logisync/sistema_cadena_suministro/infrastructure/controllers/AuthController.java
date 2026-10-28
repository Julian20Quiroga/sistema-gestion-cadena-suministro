package com.logisync.sistema_cadena_suministro.infrastructure.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logisync.sistema_cadena_suministro.application.commands.AuthLoginCommand;
import com.logisync.sistema_cadena_suministro.application.commands.AuthRegisterCommand;
import com.logisync.sistema_cadena_suministro.application.ports.driven.AuthLoginUseCase;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.AuthLoginRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.AuthRegisterRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.AuthLoginResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.AuthRegisterResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers.AuthRestMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthRestMapper mapper;
    private final AuthLoginUseCase authLoginUseCase;

    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponse> login(@RequestBody AuthLoginRequest request) {
        AuthLoginCommand command = mapper.toLoginCommand(request);
        AuthLoginResponse response = mapper.toLoginResponse(authLoginUseCase.login(command));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthRegisterResponse> postMethodName(@RequestBody AuthRegisterRequest request) {
        AuthRegisterCommand command = mapper.toRegisterCommand(request);
        AuthRegisterResponse response = mapper.toRegisterResponse(authLoginUseCase.register(command));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}

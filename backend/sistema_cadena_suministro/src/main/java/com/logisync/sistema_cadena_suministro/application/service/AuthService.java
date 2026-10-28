package com.logisync.sistema_cadena_suministro.application.service;

import org.springframework.stereotype.Service;

import com.logisync.sistema_cadena_suministro.application.commands.AuthLoginCommand;
import com.logisync.sistema_cadena_suministro.application.commands.AuthRegisterCommand;
import com.logisync.sistema_cadena_suministro.application.mappers.UserApplicationMapper;
import com.logisync.sistema_cadena_suministro.application.ports.driven.AuthLoginUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.AuthenticationPort;
import com.logisync.sistema_cadena_suministro.application.ports.driving.PasswordEncript;
import com.logisync.sistema_cadena_suministro.application.ports.driving.UserRepository;
import com.logisync.sistema_cadena_suministro.application.responses.AuthAppLoginResponse;
import com.logisync.sistema_cadena_suministro.application.responses.AuthAppRegisterResponse;
import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.models.User;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class AuthService implements AuthLoginUseCase{

    private final AuthenticationPort autheticationPort;
    private final UserApplicationMapper mapper;
    private final UserRepository repository;
    private final PasswordEncript passwordEncript;

    @Override
    public AuthAppLoginResponse login(AuthLoginCommand command) {
        String token = autheticationPort.authenticate(command.username(), command.password());
        return new AuthAppLoginResponse(token);
    }

    @Override
    public AuthAppRegisterResponse register(AuthRegisterCommand command) {
        User user = mapper.toDomain(command);
        user.setStatus(SoftDeleteStatus.ACTIVE);
        String passwordEncripted = passwordEncript.passwordEncript(user.getPasswordHash());
        return mapper.toResponse(repository.save(user, passwordEncripted));
    }
    
}

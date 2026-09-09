package com.logisync.sistema_cadena_suministro.infrastructure.controllers.mappers;

import org.mapstruct.Mapper;

import com.logisync.sistema_cadena_suministro.application.commands.AuthLoginCommand;
import com.logisync.sistema_cadena_suministro.application.commands.AuthRegisterCommand;
import com.logisync.sistema_cadena_suministro.application.responses.AuthAppLoginResponse;
import com.logisync.sistema_cadena_suministro.application.responses.AuthAppRegisterResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.AuthLoginRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.AuthRegisterRequest;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.AuthLoginResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.AuthRegisterResponse;

@Mapper (componentModel = "spring")
public interface AuthRestMapper {
    
    AuthLoginCommand toLoginCommand(AuthLoginRequest authLoginRequest);
    AuthLoginResponse toLoginResponse(AuthAppLoginResponse authLoginResponse);

    AuthRegisterCommand toRegisterCommand(AuthRegisterRequest authRegisterRequest);
    AuthRegisterResponse toRegisterResponse(AuthAppRegisterResponse authAppRegisterResponse);
}

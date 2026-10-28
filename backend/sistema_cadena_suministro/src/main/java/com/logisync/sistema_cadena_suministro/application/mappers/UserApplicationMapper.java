package com.logisync.sistema_cadena_suministro.application.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.logisync.sistema_cadena_suministro.application.commands.AuthRegisterCommand;
import com.logisync.sistema_cadena_suministro.application.responses.AuthAppRegisterResponse;
import com.logisync.sistema_cadena_suministro.domain.models.User;

@Mapper (componentModel = "spring")
public interface UserApplicationMapper {
    
    @Mapping (target = "passwordHash", source = "password")
    @Mapping (target = "status", ignore = true)
    User toDomain(AuthRegisterCommand command);
    AuthAppRegisterResponse toResponse(User user);

}

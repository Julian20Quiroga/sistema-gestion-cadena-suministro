package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.AuthLoginCommand;
import com.logisync.sistema_cadena_suministro.application.commands.AuthRegisterCommand;
import com.logisync.sistema_cadena_suministro.application.responses.AuthAppLoginResponse;
import com.logisync.sistema_cadena_suministro.application.responses.AuthAppRegisterResponse;

public interface AuthLoginUseCase {
    
    AuthAppLoginResponse login(AuthLoginCommand command);
    AuthAppRegisterResponse register(AuthRegisterCommand command);

}

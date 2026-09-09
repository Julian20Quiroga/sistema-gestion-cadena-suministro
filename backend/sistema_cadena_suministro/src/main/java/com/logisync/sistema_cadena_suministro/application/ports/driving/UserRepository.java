package com.logisync.sistema_cadena_suministro.application.ports.driving;

import com.logisync.sistema_cadena_suministro.domain.models.User;

public interface UserRepository {

    User findByUsername(String username);
    User save(User user, String passwordEncripted);
}

package com.logisync.sistema_cadena_suministro.application.ports.driving;

import com.logisync.sistema_cadena_suministro.domain.models.User;
import java.util.List;

public interface UserRepository {

    User findByUsername(String username);
    List<User> findAll();
    boolean existsByUsername(String username);
    User save(User user, String passwordEncripted);
    
}

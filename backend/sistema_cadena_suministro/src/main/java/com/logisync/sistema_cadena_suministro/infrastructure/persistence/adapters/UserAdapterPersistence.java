package com.logisync.sistema_cadena_suministro.infrastructure.persistence.adapters;

import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.logisync.sistema_cadena_suministro.application.ports.driving.UserRepository;
import com.logisync.sistema_cadena_suministro.domain.models.User;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.UserDAO;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.UserEntity;
import com.logisync.sistema_cadena_suministro.infrastructure.persistence.mappers.UserPersistenceMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserAdapterPersistence implements UserRepository {

    private UserDAO userDAO;
    private UserPersistenceMapper mapper;

    @Override
    public User findByUsername(String username) {
        Optional<UserEntity> userFound = userDAO.findByUsername(username);
        if (userFound.isEmpty()) {
            throw new UsernameNotFoundException("El usuario " + username + " no fue encontrado.");
        }
        return mapper.toDomain(userFound.get());
    }

}

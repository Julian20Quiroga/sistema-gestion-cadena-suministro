package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logisync.sistema_cadena_suministro.infrastructure.persistence.entities.UserEntity;

public interface UserDAO extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUsername(String username);
}

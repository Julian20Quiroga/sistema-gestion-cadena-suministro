package com.logisync.sistema_cadena_suministro.infrastructure.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.logisync.sistema_cadena_suministro.application.ports.driving.UserRepository;
import com.logisync.sistema_cadena_suministro.domain.models.User;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class CustomUserDetailsService implements UserDetailsService{

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userFound = userRepository.findByUsername(username);
        return new CustomUserDetail(userFound);
    }
    
}

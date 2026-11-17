package com.logisync.sistema_cadena_suministro.infrastructure.controllers;

import com.logisync.sistema_cadena_suministro.application.commands.AuthRegisterCommand;
import com.logisync.sistema_cadena_suministro.application.ports.driven.AuthLoginUseCase;
import com.logisync.sistema_cadena_suministro.application.ports.driving.UserRepository;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ConflictException;
import com.logisync.sistema_cadena_suministro.domain.models.User;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.request.CreateUserRequest;
import com.logisync.sistema_cadena_suministro.application.responses.AuthAppRegisterResponse;
import com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses.UserManagementResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PRESIDENT')")
public class UserController {

    private final UserRepository userRepository;
    private final AuthLoginUseCase authLoginUseCase;

    @GetMapping
    public ResponseEntity<List<UserManagementResponse>> findAll() {
        List<UserManagementResponse> users = userRepository.findAll().stream()
                .map(this::toResponse)
                .sorted(Comparator.comparing(UserManagementResponse::username, String.CASE_INSENSITIVE_ORDER))
                .toList();
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<AuthAppRegisterResponse> create(@Valid @RequestBody CreateUserRequest request) {
        String username = request.username().trim();
        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("Ya existe una cuenta con ese nombre de usuario.");
        }
        AuthAppRegisterResponse response = authLoginUseCase.register(
                new AuthRegisterCommand(username, request.password(), request.role()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private UserManagementResponse toResponse(User user) {
        return new UserManagementResponse(user.getUsername(), user.getRole(), user.getStatus());
    }
}

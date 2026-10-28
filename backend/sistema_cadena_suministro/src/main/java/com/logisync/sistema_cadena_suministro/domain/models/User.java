package com.logisync.sistema_cadena_suministro.domain.models;

import com.logisync.sistema_cadena_suministro.domain.enums.SoftDeleteStatus;
import com.logisync.sistema_cadena_suministro.domain.enums.UserRole;

public class User {

    private String username;
    private String passwordHash;
    private SoftDeleteStatus status;
    private UserRole role;

    public User(String username, String passwordHash, SoftDeleteStatus status, UserRole role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.status = status;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public SoftDeleteStatus getStatus() {
        return status;
    }

    public void setStatus(SoftDeleteStatus status) {
        this.status = status;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    
    
}

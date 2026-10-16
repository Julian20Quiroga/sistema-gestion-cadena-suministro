package com.logisync.sistema_cadena_suministro.domain.exceptions;

public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " not found with id: " + id);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

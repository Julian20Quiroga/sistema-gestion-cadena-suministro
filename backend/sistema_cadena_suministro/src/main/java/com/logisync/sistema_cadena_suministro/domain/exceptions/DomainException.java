package com.logisync.sistema_cadena_suministro.domain.exceptions;

public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}

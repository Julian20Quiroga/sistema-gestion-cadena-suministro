package com.logisync.sistema_cadena_suministro.domain.exceptions;

public class InvalidStateTransitionException extends DomainException {

    public InvalidStateTransitionException(String entity, String from, String action) {
        super(entity + " in state " + from + " cannot " + action);
    }
}

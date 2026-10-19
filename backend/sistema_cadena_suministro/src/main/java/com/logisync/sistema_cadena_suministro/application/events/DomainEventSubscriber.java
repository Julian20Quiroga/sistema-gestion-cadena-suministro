package com.logisync.sistema_cadena_suministro.application.events;

/** Observer: reacts to one type of domain event. */
public interface DomainEventSubscriber<E extends DomainEvent> {

    Class<E> eventType();

    void onEvent(E event);
}

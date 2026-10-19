package com.logisync.sistema_cadena_suministro.application.ports.driving;

import com.logisync.sistema_cadena_suministro.application.events.DomainEvent;

/** Observer subject: delivers events to every subscribed observer. */
public interface DomainEventPublisher {
    void publish(DomainEvent event);
}

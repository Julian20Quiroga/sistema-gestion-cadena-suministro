package com.logisync.sistema_cadena_suministro.infrastructure.events;

import com.logisync.sistema_cadena_suministro.application.events.DomainEvent;
import com.logisync.sistema_cadena_suministro.application.events.DomainEventSubscriber;
import com.logisync.sistema_cadena_suministro.application.ports.driving.DomainEventPublisher;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/** Observer subject backed by every {@link DomainEventSubscriber} bean found in the context. */
@Component
public class InMemoryDomainEventPublisher implements DomainEventPublisher {

    private final ObjectProvider<DomainEventSubscriber<?>> subscribers;

    public InMemoryDomainEventPublisher(ObjectProvider<DomainEventSubscriber<?>> subscribers) {
        this.subscribers = subscribers;
    }

    @Override
    public void publish(DomainEvent event) {
        subscribers.orderedStream()
                .filter(subscriber -> subscriber.eventType().isInstance(event))
                .forEach(subscriber -> deliver(subscriber, event));
    }

    @SuppressWarnings("unchecked")
    private <E extends DomainEvent> void deliver(DomainEventSubscriber<E> subscriber, DomainEvent event) {
        subscriber.onEvent((E) event);
    }
}

package com.logisync.sistema_cadena_suministro.application.mediator;

import com.logisync.sistema_cadena_suministro.domain.models.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Mediator: Colleague representing the orders subsystem.
 */
@Slf4j
@Component
public class OrderColleague extends Colleague {

    public OrderColleague(SupplyChainMediator mediator) {
        super(mediator);
    }

    public void onOrderCreated(Order order) {
        mediator.notifyOrderCreated(order);
    }

    public void onOrderCancelled(Order order) {
        mediator.notifyOrderCancelled(order);
    }
}

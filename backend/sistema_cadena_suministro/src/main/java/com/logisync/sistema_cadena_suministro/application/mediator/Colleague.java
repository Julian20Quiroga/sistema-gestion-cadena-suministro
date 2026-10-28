package com.logisync.sistema_cadena_suministro.application.mediator;

/**
 * Mediator: Abstract colleague that communicates with other modules only via mediator.
 */
public abstract class Colleague {

    protected final SupplyChainMediator mediator;

    protected Colleague(SupplyChainMediator mediator) {
        this.mediator = mediator;
        mediator.registerColleague(this);
    }
}

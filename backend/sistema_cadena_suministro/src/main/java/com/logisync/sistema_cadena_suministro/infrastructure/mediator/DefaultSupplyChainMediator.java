package com.logisync.sistema_cadena_suministro.infrastructure.mediator;

import com.logisync.sistema_cadena_suministro.application.mediator.Colleague;
import com.logisync.sistema_cadena_suministro.application.mediator.ProcurementColleague;
import com.logisync.sistema_cadena_suministro.application.mediator.SupplyChainMediator;
import com.logisync.sistema_cadena_suministro.domain.models.PurchaseOrder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mediator: Concrete mediator orchestrating cross-module communication.
 */
@Component
public class DefaultSupplyChainMediator implements SupplyChainMediator {

    private final List<Colleague> colleagues = new ArrayList<>();
    private ProcurementColleague procurementColleague;

    @Override
    public void registerColleague(Colleague colleague) {
        colleagues.add(colleague);
        if (colleague instanceof ProcurementColleague pc) {
            this.procurementColleague = pc;
        }
    }

    @Override
    public void notifyStockLow(Integer productId, long availableStock, int reorderPoint) {
        if (procurementColleague != null) {
            procurementColleague.handleStockLow(productId, availableStock, reorderPoint);
        }
    }

    @Override
    public void notifyPurchaseOrderCreated(PurchaseOrder order) {
        if (procurementColleague != null) {
            procurementColleague.handlePurchaseOrderCreated(order);
        }
    }
}

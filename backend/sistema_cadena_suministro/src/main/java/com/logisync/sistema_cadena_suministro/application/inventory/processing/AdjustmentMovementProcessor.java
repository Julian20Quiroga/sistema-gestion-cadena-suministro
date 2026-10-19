package com.logisync.sistema_cadena_suministro.application.inventory.processing;

import com.logisync.sistema_cadena_suministro.application.commands.RegisterAdjustmentCommand;
import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.inventory.MovementTypeFactory;
import com.logisync.sistema_cadena_suministro.domain.inventory.command.AdjustmentCommand;
import com.logisync.sistema_cadena_suministro.domain.inventory.command.StockMovementCommand;
import org.springframework.stereotype.Component;

/** Template Method step set for signed stock corrections; a reason is mandatory. */
@Component
public class AdjustmentMovementProcessor extends StockMovementProcessor<RegisterAdjustmentCommand> {

    private final LocationRules rules;

    public AdjustmentMovementProcessor(MovementContext context, LocationRules rules) {
        super(context);
        this.rules = rules;
    }

    @Override
    protected void validate(RegisterAdjustmentCommand request) {
        if (request.delta() == 0) {
            throw new BusinessRuleException("An adjustment must change the stock");
        }
        if (MovementTypeFactory.ADJUSTMENT.requiresReason()
                && (request.reason() == null || request.reason().isBlank())) {
            throw new BusinessRuleException("An adjustment requires a reason");
        }
        rules.requireActiveProduct(request.productId());
        var location = rules.requireLocation(request.storageSpaceId());
        if (request.delta() > 0) {
            rules.requireCapacityFor(location, request.delta());
        }
    }

    @Override
    protected StockMovementCommand buildCommand(RegisterAdjustmentCommand request) {
        var item = context.getInventoryRepository().findOrCreate(request.storageSpaceId(), request.productId());
        return new AdjustmentCommand(item, request.delta(), request.reason());
    }

    @Override
    protected String reasonOf(RegisterAdjustmentCommand request) {
        return request.reason();
    }

    @Override
    protected String referenceOf(RegisterAdjustmentCommand request) {
        return null;
    }
}

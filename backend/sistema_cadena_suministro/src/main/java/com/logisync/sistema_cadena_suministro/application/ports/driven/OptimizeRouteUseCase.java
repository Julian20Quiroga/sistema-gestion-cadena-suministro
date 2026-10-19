package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.OptimizeRouteCommand;
import com.logisync.sistema_cadena_suministro.application.responses.OptimizedRouteAppResponse;

public interface OptimizeRouteUseCase {
    OptimizedRouteAppResponse optimizeRoute(OptimizeRouteCommand command);
}

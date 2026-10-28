package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.responses.MovementAppResponse;

public interface UndoMovementUseCase {
    MovementAppResponse undo(Integer movementId);
}

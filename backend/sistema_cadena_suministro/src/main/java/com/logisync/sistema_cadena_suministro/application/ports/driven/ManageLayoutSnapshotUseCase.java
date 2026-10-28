package com.logisync.sistema_cadena_suministro.application.ports.driven;

import com.logisync.sistema_cadena_suministro.application.commands.CreateLayoutSnapshotCommand;
import com.logisync.sistema_cadena_suministro.application.responses.LayoutSnapshotAppResponse;

import java.util.List;

public interface ManageLayoutSnapshotUseCase {
    LayoutSnapshotAppResponse createSnapshot(CreateLayoutSnapshotCommand command);
    List<LayoutSnapshotAppResponse> listSnapshots(Integer warehouseId);
    LayoutSnapshotAppResponse restoreSnapshot(Integer warehouseId, Integer snapshotId);
}

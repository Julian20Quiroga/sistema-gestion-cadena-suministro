package com.logisync.sistema_cadena_suministro.domain.inventory;

import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.inventory.command.AdjustmentCommand;
import com.logisync.sistema_cadena_suministro.domain.inventory.command.DispatchExitCommand;
import com.logisync.sistema_cadena_suministro.domain.inventory.command.EntryCommand;
import com.logisync.sistema_cadena_suministro.domain.inventory.command.ExitCommand;
import com.logisync.sistema_cadena_suministro.domain.inventory.command.TransferCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StockMovementCommandTest {

    private InventoryItem item(int physical, int reserved) {
        return new InventoryItem(1, 10, 100, physical, reserved, 0, 1);
    }

    @Test
    void entryExecuteAndUndoRestoreStock() {
        InventoryItem item = item(5, 0);
        EntryCommand command = new EntryCommand(item, 7);

        command.execute();
        assertEquals(12, item.getPhysicalQuantity());

        command.undo();
        assertEquals(5, item.getPhysicalQuantity());
    }

    @Test
    void exitCannotLeaveNegativeStock() {
        InventoryItem item = item(3, 0);
        assertThrows(BusinessRuleException.class, () -> new ExitCommand(item, 4).execute());
        assertEquals(3, item.getPhysicalQuantity());
    }

    @Test
    void exitCannotConsumeReservedUnits() {
        InventoryItem item = item(10, 8);
        assertThrows(BusinessRuleException.class, () -> new ExitCommand(item, 5).execute());
    }

    @Test
    void dispatchExitConsumesPhysicalAndReserved() {
        InventoryItem item = item(10, 6);
        DispatchExitCommand command = new DispatchExitCommand(item, 6);

        command.execute();
        assertEquals(4, item.getPhysicalQuantity());
        assertEquals(0, item.getReservedQuantity());

        command.undo();
        assertEquals(10, item.getPhysicalQuantity());
        assertEquals(6, item.getReservedQuantity());
    }

    @Test
    void adjustmentRequiresReason() {
        InventoryItem item = item(3, 0);
        assertThrows(BusinessRuleException.class, () -> new AdjustmentCommand(item, -1, " "));
        new AdjustmentCommand(item, -1, "Damaged").execute();
        assertEquals(2, item.getPhysicalQuantity());
    }

    @Test
    void transferMovesUnitsAndUndoReversesBothSides() {
        InventoryItem source = item(10, 0);
        InventoryItem target = new InventoryItem(2, 10, 101, 1, 0, 0, 1);
        TransferCommand command = new TransferCommand(source, target, 4);

        command.execute();
        assertEquals(6, source.getPhysicalQuantity());
        assertEquals(5, target.getPhysicalQuantity());

        command.undo();
        assertEquals(10, source.getPhysicalQuantity());
        assertEquals(1, target.getPhysicalQuantity());
    }
}

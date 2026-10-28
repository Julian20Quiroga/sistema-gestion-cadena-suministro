package com.logisync.sistema_cadena_suministro.domain.inventory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MovementTypeFactoryTest {

    @Test
    void returnsTheSameSharedInstancePerCode() {
        assertSame(MovementTypeFactory.of("ENTRADA"), MovementTypeFactory.of(" entrada "));
        assertSame(MovementTypeFactory.ENTRY, MovementTypeFactory.of("ENTRADA"));
        assertEquals(4, MovementTypeFactory.cachedInstances());
    }

    @Test
    void adjustmentRequiresReasonButEntryDoesNot() {
        assertTrue(MovementTypeFactory.ADJUSTMENT.requiresReason());
        assertFalse(MovementTypeFactory.ENTRY.requiresReason());
    }

    @Test
    void rejectsUnknownCodes() {
        assertThrows(IllegalArgumentException.class, () -> MovementTypeFactory.of("OTHER"));
    }
}

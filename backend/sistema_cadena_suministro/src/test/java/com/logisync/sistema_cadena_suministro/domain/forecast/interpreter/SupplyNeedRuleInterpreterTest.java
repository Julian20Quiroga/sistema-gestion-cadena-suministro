package com.logisync.sistema_cadena_suministro.domain.forecast.interpreter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SupplyNeedRuleInterpreterTest {

    private final SupplyNeedRuleInterpreter interpreter = new SupplyNeedRuleInterpreter();

    @Test
    void createsNeedWhenAutomaticCreationHasShortageAndNoOpenNeedExists() {
        assertTrue(interpreter.shouldCreate(new SupplyNeedRuleContext(true, 4, false)));
    }

    @Test
    void doesNotCreateNeedWhenAutomaticCreationIsDisabled() {
        assertFalse(interpreter.shouldCreate(new SupplyNeedRuleContext(false, 4, false)));
    }

    @Test
    void doesNotCreateNeedWhenThereIsNoForecastShortfall() {
        assertFalse(interpreter.shouldCreate(new SupplyNeedRuleContext(true, 0, false)));
    }

    @Test
    void doesNotCreateDuplicateOpenNeed() {
        assertFalse(interpreter.shouldCreate(new SupplyNeedRuleContext(true, 4, true)));
    }
}
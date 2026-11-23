import test from 'node:test';
import assert from 'node:assert/strict';
import { ForecastRuleInterpreter } from '../../../frontend/src/shared/patterns/behavioral/ForecastRuleInterpreter.js';

test('Interpreter identifica faltante por cantidad recomendada o demanda', () => {
  const interpreter = new ForecastRuleInterpreter();
  assert.equal(interpreter.interpret({ recommendedProcurementQuantity: 3 }), true);
  assert.equal(interpreter.interpret({ totalProjectedDemand: 20, currentAvailableStock: 10 }), true);
  assert.equal(interpreter.interpret({ totalProjectedDemand: 8, currentAvailableStock: 10 }), false);
});

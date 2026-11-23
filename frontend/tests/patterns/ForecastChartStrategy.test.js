import test from 'node:test';
import assert from 'node:assert/strict';
import { ForecastChartStrategy } from '../../../frontend/src/shared/patterns/behavioral/ForecastChartStrategy.js';

test('Strategy selecciona presentación del gráfico por algoritmo', () => {
  const forecast = { historicalPoints: [10, 12], projections: [14] };
  const moving = ForecastChartStrategy.for('MOVING_AVERAGE')(forecast);
  const exponential = ForecastChartStrategy.for('EXPONENTIAL_SMOOTHING')(forecast);
  const regression = ForecastChartStrategy.for('LINEAR_REGRESSION')(forecast);

  assert.deepEqual(moving.historical, [10, 12]);
  assert.deepEqual(moving.projected, [14]);
  assert.notEqual(moving.caption, exponential.caption);
  assert.notEqual(exponential.caption, regression.caption);
});

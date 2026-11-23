import test from 'node:test';
import assert from 'node:assert/strict';
import { DispatchStatePolicy } from '../../../frontend/src/shared/patterns/behavioral/DispatchStatePolicy.js';

test('State ofrece acciones según el estado del despacho', () => {
  const policy = new DispatchStatePolicy();
  assert.deepEqual(policy.actionsFor('PENDIENTE').map(({ action }) => action), ['prepare', 'cancel']);
  assert.deepEqual(policy.actionsFor('PREPARANDO').map(({ action }) => action), ['ship', 'cancel']);
  assert.deepEqual(policy.actionsFor('EN_TRANSPORTE').map(({ action }) => action), ['deliver']);
  assert.deepEqual(policy.actionsFor('ENTREGADO'), []);
  assert.deepEqual(policy.actionsFor('CANCELADO'), []);
});

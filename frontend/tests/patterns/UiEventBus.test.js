import test from 'node:test';
import assert from 'node:assert/strict';
import { uiEventBus } from '../../../frontend/src/shared/patterns/behavioral/UiEventBus.js';

test('Event Bus publica eventos y deja de notificar al cancelar la suscripción', () => {
  const received = [];
  const handler = (value) => received.push(value);
  const unsubscribe = uiEventBus.subscribe('test:evidence', handler);
  uiEventBus.publish('test:evidence', { ok: true });
  unsubscribe();
  uiEventBus.publish('test:evidence', { ok: false });
  assert.deepEqual(received, [{ ok: true }]);
});

import test from 'node:test';
import assert from 'node:assert/strict';
import { InventoryEntryCommand } from '../../../frontend/src/shared/patterns/behavioral/InventoryEntryCommand.js';
import { uiEventBus } from '../../../frontend/src/shared/patterns/behavioral/UiEventBus.js';

test('Command ejecuta el registro y publica el cambio de inventario', async () => {
  const form = { productId: 7, warehouseId: 2, storageSpaceId: 12, quantity: 3 };
  let request;
  let event;
  const unsubscribe = uiEventBus.subscribe('inventory:changed', (payload) => { event = payload; });
  try {
    const command = new InventoryEntryCommand({
      token: 'demo-token',
      form,
      register: async (value) => { request = value; return { movementId: 18 }; },
    });
    const result = await command.execute();
    assert.deepEqual(request, { token: 'demo-token', ...form });
    assert.deepEqual(result, { movementId: 18 });
    assert.deepEqual(event, { productId: 7, warehouseId: 2 });
  } finally {
    unsubscribe();
  }
});

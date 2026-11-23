import test from 'node:test';
import assert from 'node:assert/strict';
import { InventoryEntryMediator } from '../../../frontend/src/shared/patterns/behavioral/InventoryEntryMediator.js';

test('Mediator coordina cambios de bodega y producto en el formulario', () => {
  let form = { warehouseId: '1', storageSpaceId: '9', productId: '3' };
  const errors = [];
  const successes = [];
  const mediator = new InventoryEntryMediator({
    setForm: (update) => { form = update(form); },
    setError: (value) => errors.push(value),
    setSuccess: (value) => successes.push(value),
  });

  mediator.warehouseChanged('2');
  assert.deepEqual(form, { warehouseId: '2', storageSpaceId: '', productId: '3' });
  assert.equal(errors.at(-1), '');
  assert.equal(successes.at(-1), '');

  mediator.productChanged('8');
  assert.equal(form.productId, '8');
  assert.equal(errors.at(-1), '');
});

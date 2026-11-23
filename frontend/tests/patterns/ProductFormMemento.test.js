import test from 'node:test';
import assert from 'node:assert/strict';
import { ProductFormMemento } from '../../../frontend/src/shared/patterns/behavioral/ProductFormMemento.js';

test('Memento restaura los valores originales del formulario', () => {
  const original = { name: 'Celular prueba', sku: 'SKU-1', salePrice: '900000' };
  const memento = new ProductFormMemento(original);
  original.name = 'Valor alterado';
  assert.deepEqual(memento.restore(), {
    name: 'Celular prueba',
    sku: 'SKU-1',
    salePrice: '900000',
  });
});

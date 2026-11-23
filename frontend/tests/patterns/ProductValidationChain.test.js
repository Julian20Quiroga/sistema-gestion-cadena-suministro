import test from 'node:test';
import assert from 'node:assert/strict';
import { ProductValidationChain } from '../../../frontend/src/shared/patterns/behavioral/ProductValidationChain.js';

test('Chain rechaza obligatorios vacíos y números fuera de rango', () => {
  const chain = new ProductValidationChain();
  assert.equal(chain.validate({ name: '', sku: '', salePrice: '', weightKg: '', reorderPoint: '0' }), 'Completa los campos obligatorios.');
  assert.match(chain.validate({ name: 'Equipo', sku: 'A-1', salePrice: '100', weightKg: '0', reorderPoint: '0' }), /peso debe ser mayor que cero/);
});

test('Chain permite un producto con datos válidos', () => {
  const chain = new ProductValidationChain();
  assert.equal(chain.validate({ name: 'Equipo', sku: 'A-1', salePrice: '100', weightKg: '1', reorderPoint: '0' }), '');
});

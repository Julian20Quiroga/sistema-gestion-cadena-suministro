import test from 'node:test';
import assert from 'node:assert/strict';
import { CollectionIterator } from '../../../frontend/src/shared/patterns/behavioral/CollectionIterator.js';

test('Iterator recorre los elementos en orden y termina la colección', () => {
  const iterator = new CollectionIterator(['A', 'B']);
  assert.equal(iterator.hasNext(), true);
  assert.equal(iterator.next(), 'A');
  assert.equal(iterator.next(), 'B');
  assert.equal(iterator.hasNext(), false);
  assert.equal(iterator.next(), undefined);
});

test('Iterator convierte una colección en arreglo sin alterar el origen', () => {
  const items = [1, 2, 3];
  assert.deepEqual(CollectionIterator.toArray(items), items);
  assert.deepEqual(items, [1, 2, 3]);
});

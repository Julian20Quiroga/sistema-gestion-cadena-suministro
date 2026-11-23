import test from 'node:test';
import assert from 'node:assert/strict';
import { ForecastResultElement, ForecastSummaryVisitor } from '../../../frontend/src/shared/patterns/behavioral/ForecastSummaryVisitor.js';

test('Visitor consolida productos, faltantes y unidades sugeridas', () => {
  const visitor = new ForecastSummaryVisitor();
  const results = [
    new ForecastResultElement({ recommendedProcurementQuantity: 5 }),
    new ForecastResultElement({ recommendedProcurementQuantity: 0 }),
    new ForecastResultElement({ recommendedProcurementQuantity: 2 }),
  ];
  assert.deepEqual(visitor.visit(results), {
    products: 3,
    productsWithShortage: 2,
    suggestedUnits: 7,
  });
});

import test from 'node:test';
import assert from 'node:assert/strict';
import { FormSubmissionTemplate } from '../../../frontend/src/shared/patterns/behavioral/FormSubmissionTemplate.js';

test('Template Method valida, envía y libera el estado de carga', async () => {
  const template = new FormSubmissionTemplate();
  const busy = [];
  let saved;
  const result = await template.run({
    validate: () => '',
    request: async () => ({ id: 4 }),
    onSuccess: (value) => { saved = value; },
    onError: () => assert.fail('No debía ejecutarse onError'),
    setBusy: (value) => busy.push(value),
    setError: () => {},
  });
  assert.equal(result, true);
  assert.deepEqual(saved, { id: 4 });
  assert.deepEqual(busy, [true, false]);
});

test('Template Method detiene el envío si falla la validación', async () => {
  const template = new FormSubmissionTemplate();
  let requestCalled = false;
  let error;
  const result = await template.run({
    validate: () => 'Campo requerido',
    request: async () => { requestCalled = true; },
    onSuccess: () => {},
    onError: () => {},
    setBusy: () => assert.fail('No debe iniciar carga'),
    setError: (value) => { error = value; },
  });
  assert.equal(result, false);
  assert.equal(requestCalled, false);
  assert.equal(error, 'Campo requerido');
});

export class FormSubmissionTemplate {
  async run({ validate, request, onSuccess, onError, setBusy, setError }) {
    setError('');
    const validationError = validate();
    if (validationError) { setError(validationError); return false; }
    setBusy(true);
    try { const result = await request(); onSuccess(result); return true; }
    catch (error) { onError(error); return false; }
    finally { setBusy(false); }
  }
}

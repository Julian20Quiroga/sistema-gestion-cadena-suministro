class ValidationHandler {
  setNext(handler) { this.next = handler; return handler; }
  handle(form) { return this.next ? this.next.handle(form) : ''; }
}
export class RequiredProductFields extends ValidationHandler {
  handle(form) {
    if (!form.name.trim() || !form.sku.trim() || !form.salePrice || !form.weightKg) return 'Completa los campos obligatorios.';
    return super.handle(form);
  }
}
export class ValidProductNumbers extends ValidationHandler {
  handle(form) {
    if (Number(form.salePrice) < 0 || Number(form.weightKg) <= 0 || Number(form.reorderPoint) < 0) return 'El precio y el punto de reposicion no pueden ser negativos; el peso debe ser mayor que cero.';
    return super.handle(form);
  }
}
export class ProductValidationChain {
  constructor() { const required = new RequiredProductFields(); required.setNext(new ValidProductNumbers()); this.first = required; }
  validate(form) { return this.first.handle(form); }
}

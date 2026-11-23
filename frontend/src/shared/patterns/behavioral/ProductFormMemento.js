export class ProductFormMemento {
  constructor(snapshot) { this.snapshot = { ...snapshot }; }
  restore() { return { ...this.snapshot }; }
}

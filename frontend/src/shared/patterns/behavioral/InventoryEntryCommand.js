import { uiEventBus } from './UiEventBus.js';

export class InventoryEntryCommand {
  constructor({ token, form, register }) { this.token = token; this.form = form; this.register = register; }
  async execute() {
    const result = await this.register({ token: this.token, ...this.form });
    uiEventBus.publish('inventory:changed', { productId: this.form.productId, warehouseId: this.form.warehouseId });
    return result;
  }
}

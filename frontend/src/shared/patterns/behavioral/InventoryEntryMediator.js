export class InventoryEntryMediator {
  constructor({ setForm, setError, setSuccess }) { Object.assign(this, { setForm, setError, setSuccess }); }
  warehouseChanged(warehouseId) { this.setForm((form) => ({ ...form, warehouseId, storageSpaceId: '' })); this.setError(''); this.setSuccess(''); }
  productChanged(productId) { this.setForm((form) => ({ ...form, productId })); this.setError(''); }
}

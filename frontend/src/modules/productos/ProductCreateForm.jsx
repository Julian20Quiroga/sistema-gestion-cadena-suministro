import { useEffect, useState } from 'react';
import {
  createProduct,
  fetchSuppliers,
  updateProduct,
} from './productService';

const emptyForm = {
  name: '',
  supplierId: '',
  sku: '',
  description: '',
  model: '',
  ram: '',
  storage: '',
  processor: '',
  color: '',
  salePrice: '',
  weightKg: '',
  reorderPoint: '0',
  status: 'ACTIVE',
};

function formFromProduct(product) {
  if (!product) return { ...emptyForm };

  return {
    name: product.name || '',
    supplierId: String(product.supplierId || ''),
    sku: product.sku || '',
    description: product.description || '',
    model: product.model || '',
    ram: product.ram == null ? '' : String(product.ram),
    storage: product.storage == null ? '' : String(product.storage),
    processor: product.processor || '',
    color: product.color || '',
    salePrice: product.salePrice == null ? '' : String(product.salePrice),
    weightKg: product.weightKg == null ? '' : String(product.weightKg),
    reorderPoint: product.reorderPoint == null ? '0' : String(product.reorderPoint),
    status: product.status || 'ACTIVE',
  };
}

function ProductCreateForm({ token, productToEdit, onCancel, onSaved }) {
  const isEditing = Boolean(productToEdit);
  const [formData, setFormData] = useState(() => formFromProduct(productToEdit));
  const [suppliers, setSuppliers] = useState([]);
  const [loadingSuppliers, setLoadingSuppliers] = useState(true);
  const [supplierError, setSupplierError] = useState('');
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState('');

  useEffect(() => {
    let isCurrentRequest = true;

    fetchSuppliers({ token, page: 0, size: 100 })
      .then((result) => {
        if (isCurrentRequest) {
          const activeSuppliers = (result.content || []).filter(
            (supplier) => supplier.status === 'ACTIVE'
          );
          setSuppliers(activeSuppliers);
        }
      })
      .catch(() => {
        if (isCurrentRequest) setSupplierError('No fue posible cargar los proveedores.');
      })
      .finally(() => {
        if (isCurrentRequest) setLoadingSuppliers(false);
      });

    return () => {
      isCurrentRequest = false;
    };
  }, [token]);

  function handleChange(event) {
    const { name, value } = event.target;
    setFormData((current) => ({ ...current, [name]: value }));
    setFormError('');
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setSaving(true);
    setFormError('');

    const product = {
      name: formData.name.trim(),
      supplierId: formData.supplierId === "" ? null : Number(formData.supplierId),
      sku: formData.sku.trim(),
      description: formData.description.trim() || null,
      model: formData.model.trim() || null,
      ram: formData.ram === '' ? null : Number(formData.ram),
      storage: formData.storage === '' ? null : Number(formData.storage),
      processor: formData.processor.trim() || null,
      color: formData.color.trim() || null,
      salePrice: Number(formData.salePrice),
      weightKg: Number(formData.weightKg),
      reorderPoint: Number(formData.reorderPoint),
      categoryIds: [],
      ...(isEditing ? { status: formData.status } : {}),
    };

    try {
      if (isEditing) {
        await updateProduct({ token, id: productToEdit.id, product });
      } else {
        await createProduct({ token, product });
      }
      onSaved(isEditing ? 'Producto actualizado correctamente.' : 'Producto registrado correctamente.');
    } catch (requestError) {
      const detail = requestError.response?.data?.detail
        || requestError.response?.data?.message;
      setFormError(detail || 'No fue posible guardar el producto. Revisa los datos e inténtalo de nuevo.');
    } finally {
      setSaving(false);
    }
  }

  return (
    <section className="products-form-card" aria-labelledby="product-form-title">
      <h2 id="product-form-title">{isEditing ? 'Actualizar producto' : 'Registrar producto'}</h2>
      {formError && <p className="products-form-error" role="alert">{formError}</p>}
      {supplierError && <p className="products-form-error" role="alert">{supplierError}</p>}
      {!isEditing && !loadingSuppliers && !supplierError && suppliers.length === 0 && (
        <p className="products-form-note">
          No hay proveedores activos disponibles. Registra o activa un proveedor antes de guardar productos.
        </p>
      )}

      <form onSubmit={handleSubmit}>
        <div className="products-form-grid">
          <label className="products-form-field">
            Nombre *
            <input name="name" value={formData.name} onChange={handleChange} maxLength="100" required />
          </label>
          <label className="products-form-field">
            SKU *
            <input name="sku" value={formData.sku} onChange={handleChange} maxLength="50" required />
          </label>
          <label className="products-form-field">
            {isEditing ? "Proveedor" : "Proveedor *"}
            <select
              name="supplierId"
              value={formData.supplierId}
              onChange={handleChange}
              required={!isEditing}
              disabled={loadingSuppliers || (!isEditing && suppliers.length === 0)}
            >
              <option value="">
                {isEditing ? 'Sin proveedor asignado' : loadingSuppliers ? 'Cargando proveedores…' : 'Selecciona un proveedor'}
              </option>
              {suppliers.map((supplier) => (
                <option key={supplier.id} value={supplier.id}>
                  {supplier.name}{supplier.nit ? ' — NIT ' + supplier.nit : ''}
                </option>
              ))}
            </select>
          </label>
          <label className="products-form-field">
            Precio de venta (COP) *
            <input name="salePrice" type="number" min="0" step="0.01" value={formData.salePrice} onChange={handleChange} required />
          </label>
          <label className="products-form-field">
            Peso (kg) *
            <input name="weightKg" type="number" min="0.001" step="0.001" value={formData.weightKg} onChange={handleChange} required />
          </label>
          <label className="products-form-field">
            Punto de reposición *
            <input name="reorderPoint" type="number" min="0" step="1" value={formData.reorderPoint} onChange={handleChange} required />
          </label>
          <label className="products-form-field">
            Modelo
            <input name="model" value={formData.model} onChange={handleChange} maxLength="100" />
          </label>
          <label className="products-form-field">
            Procesador
            <input name="processor" value={formData.processor} onChange={handleChange} maxLength="100" />
          </label>
          <label className="products-form-field">
            RAM (GB)
            <input name="ram" type="number" min="0" step="1" value={formData.ram} onChange={handleChange} />
          </label>
          <label className="products-form-field">
            Almacenamiento (GB)
            <input name="storage" type="number" min="0" step="1" value={formData.storage} onChange={handleChange} />
          </label>
          <label className="products-form-field">
            Color
            <input name="color" value={formData.color} onChange={handleChange} maxLength="50" />
          </label>
          <label className="products-form-field products-form-field-wide">
            Descripción
            <textarea name="description" value={formData.description} onChange={handleChange} maxLength="500" rows="3" />
          </label>
          {isEditing && (
            <label className="products-form-field">
              Estado
              <select name="status" value={formData.status} onChange={handleChange}>
                <option value="ACTIVE">Activo</option>
                <option value="DELETED">Eliminado</option>
              </select>
            </label>
          )}
        </div>

        <div className="products-form-actions">
          <button
            className="products-primary-button"
            type="submit"
            disabled={saving || loadingSuppliers || (!isEditing && suppliers.length === 0)}
          >
            {saving ? 'Guardando…' : isEditing ? 'Guardar cambios' : 'Guardar producto'}
          </button>
          <button className="products-secondary-button" type="button" onClick={onCancel} disabled={saving}>
            Cancelar
          </button>
        </div>
      </form>
    </section>
  );
}

export default ProductCreateForm;







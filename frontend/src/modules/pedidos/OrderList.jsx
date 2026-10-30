import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../shared/context/AuthContext';
import { createOrder, fetchOrders, fetchProducts, transitionOrder } from './orderService';
import './OrderList.css';

const statusOptions = [
  { value: 'REGISTRADO', label: 'Registrado' },
  { value: 'PREPARACION', label: 'En preparación' },
  { value: 'DESPACHADO', label: 'Despachado' },
  { value: 'ENTREGADO', label: 'Entregado' },
  { value: 'ANULADO', label: 'Anulado' },
];

const statusLabels = Object.fromEntries(statusOptions.map((item) => [item.value, item.label]));

const money = new Intl.NumberFormat('es-CO', {
  style: 'currency',
  currency: 'COP',
  maximumFractionDigits: 2,
});

const emptyItem = () => ({ productId: '', quantity: '1', unitPrice: '' });

const emptyForm = () => ({
  orderReference: '',
  deliveryAddress: '',
  deliveryCity: '',
  deliveryCountry: '',
  customerPhone: '',
  customerName: '',
  deliveryLatitude: '',
  deliveryLongitude: '',
  items: [emptyItem()],
});

function apiErrorMessage(error, fallback) {
  return error.response?.data?.detail
    || error.response?.data?.message
    || error.response?.data?.error
    || fallback;
}

function OrderList() {
  const { token, role } = useAuth();
  const navigate = useNavigate();
  const canManageOrders = ['PRESIDENT', 'ROLE_PRESIDENT', 'BRANCH_ADMIN', 'ROLE_BRANCH_ADMIN'].includes(role);

  const [orders, setOrders] = useState([]);
  const [products, setProducts] = useState([]);
  const [productsError, setProductsError] = useState('');
  const [referenceText, setReferenceText] = useState('');
  const [statusText, setStatusText] = useState('');
  const [reference, setReference] = useState('');
  const [status, setStatus] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [loadingProducts, setLoadingProducts] = useState(true);
  const [error, setError] = useState('');
  const [actionError, setActionError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);
  const [pendingAction, setPendingAction] = useState('');
  const [expandedOrderId, setExpandedOrderId] = useState(null);
  const [refreshKey, setRefreshKey] = useState(0);

  const productById = useMemo(
    () => new Map(products.map((product) => [Number(product.id), product])),
    [products]
  );

  useEffect(() => {
    let current = true;

    async function loadOrders() {
      setLoading(true);
      setError('');
      try {
        const result = await fetchOrders({ token, reference, status, page, size: 10 });
        if (!current) return;
        setOrders(result.content || []);
        setTotalPages(result.totalPages || 0);
      } catch (requestError) {
        if (current) setError(apiErrorMessage(requestError, 'No fue posible cargar los pedidos.'));
      } finally {
        if (current) setLoading(false);
      }
    }

    loadOrders();
    return () => { current = false; };
  }, [token, reference, status, page, refreshKey]);

  useEffect(() => {
    let current = true;

    async function loadProducts() {
      setLoadingProducts(true);
      setProductsError('');
      try {
        const result = await fetchProducts({ token, page: 0, size: 100 });
        if (current) setProducts(result.content || []);
      } catch {
        if (current) setProductsError('No fue posible cargar la lista de productos para el pedido.');
      } finally {
        if (current) setLoadingProducts(false);
      }
    }

    loadProducts();
    return () => { current = false; };
  }, [token]);

  function handleSearch(event) {
    event.preventDefault();
    setPage(0);
    setReference(referenceText.trim());
    setStatus(statusText);
  }

  function openForm() {
    setForm(emptyForm());
    setProductsError('');
    setActionError('');
    setSuccessMessage('');
    setShowForm(true);
  }

  function updateForm(field, value) {
    setForm((current) => ({ ...current, [field]: value }));
  }

  function updateItem(index, field, value) {
    setForm((current) => ({
      ...current,
      items: current.items.map((item, itemIndex) => {
        if (itemIndex !== index) return item;
        if (field === 'productId') {
          const product = products.find((candidate) => String(candidate.id) === value);
          return { ...item, productId: value, unitPrice: product ? String(product.salePrice ?? '') : '' };
        }
        return { ...item, [field]: value };
      }),
    }));
  }

  function addItem() {
    setForm((current) => ({ ...current, items: [...current.items, emptyItem()] }));
  }

  function removeItem(index) {
    setForm((current) => ({
      ...current,
      items: current.items.length === 1
        ? current.items
        : current.items.filter((_, itemIndex) => itemIndex !== index),
    }));
  }

  async function handleCreate(event) {
    event.preventDefault();
    setSaving(true);
    setActionError('');
    setSuccessMessage('');

    const order = {
      orderReference: form.orderReference.trim(),
      deliveryAddress: form.deliveryAddress.trim(),
      deliveryCity: form.deliveryCity.trim(),
      deliveryCountry: form.deliveryCountry.trim(),
      customerPhone: form.customerPhone.trim(),
      customerName: form.customerName.trim(),
      deliveryLatitude: form.deliveryLatitude === '' ? null : Number(form.deliveryLatitude),
      deliveryLongitude: form.deliveryLongitude === '' ? null : Number(form.deliveryLongitude),
      items: form.items.map((item) => ({
        productId: Number(item.productId),
        quantity: Number(item.quantity),
        unitPrice: Number(item.unitPrice),
      })),
    };

    try {
      await createOrder({ token, order });
      setShowForm(false);
      setForm(emptyForm());
      setPage(0);
      setReference('');
      setReferenceText('');
      setStatus('');
      setStatusText('');
      setRefreshKey((current) => current + 1);
      setSuccessMessage('Pedido creado correctamente.');
    } catch (requestError) {
      setActionError(apiErrorMessage(requestError, 'No fue posible crear el pedido. Revisa los datos y la disponibilidad.'));
    } finally {
      setSaving(false);
    }
  }

  async function handleTransition(order, action) {
    if (action === 'cancel' && !window.confirm('¿Deseas anular el pedido ' + order.orderReference + '?')) return;

    setPendingAction(order.id + ':' + action);
    setActionError('');
    setSuccessMessage('');
    try {
      await transitionOrder({ token, id: order.id, action });
      setRefreshKey((current) => current + 1);
      setSuccessMessage('Estado del pedido actualizado.');
    } catch (requestError) {
      setActionError(apiErrorMessage(requestError, 'No fue posible cambiar el estado del pedido.'));
    } finally {
      setPendingAction('');
    }
  }


  return (
    <main className="orders-page">
      <header className="orders-header">
        <div>
          <button className="orders-back-button" type="button" onClick={() => navigate('/dashboard')}>
            ← Volver al dashboard
          </button>
          <h1>Pedidos</h1>
          <p>Consulta y gestiona los pedidos de clientes de LogiSync.</p>
        </div>
        {canManageOrders && (
          <button className="orders-primary-button" type="button" onClick={openForm}>
            Nuevo pedido
          </button>
        )}
      </header>

      {successMessage && <p className="orders-success" role="status">{successMessage}</p>}
      {actionError && <p className="orders-error" role="alert">{actionError}</p>}

      {showForm && canManageOrders && (
        <form className="orders-form-card" onSubmit={handleCreate}>
          <h2>Registrar pedido</h2>
          <p className="orders-form-intro">Completa los datos del cliente y agrega los productos solicitados.</p>
          {productsError && <p className="orders-error" role="alert">{productsError}</p>}
          {loadingProducts && <p className="orders-muted">Cargando productos…</p>}

          <div className="orders-form-grid">
            <label className="orders-field">
              Referencia del pedido *
              <input required maxLength="80" value={form.orderReference} onChange={(event) => updateForm('orderReference', event.target.value)} />
            </label>
            <label className="orders-field">
              Nombre del cliente *
              <input required maxLength="150" value={form.customerName} onChange={(event) => updateForm('customerName', event.target.value)} />
            </label>
            <label className="orders-field">
              Teléfono *
              <input required type="tel" maxLength="30" value={form.customerPhone} onChange={(event) => updateForm('customerPhone', event.target.value)} />
            </label>
            <label className="orders-field">
              País de entrega *
              <input required maxLength="100" value={form.deliveryCountry} onChange={(event) => updateForm('deliveryCountry', event.target.value)} />
            </label>
            <label className="orders-field">
              Ciudad de entrega *
              <input required maxLength="100" value={form.deliveryCity} onChange={(event) => updateForm('deliveryCity', event.target.value)} />
            </label>
            <label className="orders-field orders-field-wide">
              Dirección de entrega *
              <input required maxLength="250" value={form.deliveryAddress} onChange={(event) => updateForm('deliveryAddress', event.target.value)} />
            </label>
            <label className="orders-field">
              Latitud (opcional)
              <input type="number" step="any" value={form.deliveryLatitude} onChange={(event) => updateForm('deliveryLatitude', event.target.value)} />
            </label>
            <label className="orders-field">
              Longitud (opcional)
              <input type="number" step="any" value={form.deliveryLongitude} onChange={(event) => updateForm('deliveryLongitude', event.target.value)} />
            </label>
          </div>

          <div className="orders-items-heading">
            <h3>Productos del pedido</h3>
            <button className="orders-secondary-button" type="button" onClick={addItem} disabled={loadingProducts || products.length === 0}>
              Agregar producto
            </button>
          </div>

          {form.items.map((item, index) => (
            <div className="orders-item-row" key={index}>
              <label className="orders-field">
                Producto *
                <select
                  required
                  value={item.productId}
                  onChange={(event) => updateItem(index, 'productId', event.target.value)}
                  disabled={loadingProducts || products.length === 0}
                >
                  <option value="">Selecciona un producto</option>
                  {products.map((product) => (
                    <option key={product.id} value={product.id}>
                      {(product.name || 'Producto') + ' — ' + (product.sku || 'sin SKU')}
                    </option>
                  ))}
                </select>
              </label>
              <label className="orders-field">
                Cantidad *
                <input
                  required
                  type="number"
                  min="1"
                  step="1"
                  value={item.quantity}
                  onChange={(event) => updateItem(index, 'quantity', event.target.value)}
                />
              </label>
              <label className="orders-field">
                Precio unitario *
                <input
                  required
                  type="number"
                  min="0.01"
                  step="0.01"
                  value={item.unitPrice}
                  onChange={(event) => updateItem(index, 'unitPrice', event.target.value)}
                />
              </label>
              <button
                className="orders-remove-item"
                type="button"
                onClick={() => removeItem(index)}
                disabled={form.items.length === 1}
                aria-label="Quitar producto"
              >
                Quitar
              </button>
            </div>
          ))}

          {!loadingProducts && products.length === 0 && (
            <p className="orders-muted">No hay productos disponibles para agregar a un pedido.</p>
          )}
          <div className="orders-form-actions">
            <button className="orders-primary-button" type="submit" disabled={saving || loadingProducts || products.length === 0}>
              {saving ? 'Guardando…' : 'Crear pedido'}
            </button>
            <button className="orders-secondary-button" type="button" onClick={() => setShowForm(false)} disabled={saving}>
              Cancelar
            </button>
          </div>
        </form>
      )}

      <form className="orders-filters" onSubmit={handleSearch}>
        <label className="orders-field">
          Buscar por referencia
          <input value={referenceText} onChange={(event) => setReferenceText(event.target.value)} placeholder="Escribe una referencia" />
        </label>
        <label className="orders-field">
          Estado
          <select value={statusText} onChange={(event) => setStatusText(event.target.value)}>
            <option value="">Todos los estados</option>
            {statusOptions.map((item) => <option key={item.value} value={item.value}>{item.label}</option>)}
          </select>
        </label>
        <button className="orders-primary-button" type="submit">Buscar</button>
      </form>

      <section className="orders-table-card" aria-live="polite">
        {loading ? (
          <p className="orders-message">Cargando pedidos…</p>
        ) : error ? (
          <p className="orders-message orders-error">{error}</p>
        ) : orders.length === 0 ? (
          <p className="orders-message">No se encontraron pedidos para estos filtros.</p>
        ) : (
          <div className="orders-table-scroll">
            <table className="orders-table">
              <thead>
                <tr>
                  <th scope="col">Referencia</th>
                  <th scope="col">Cliente</th>
                  <th scope="col">Ciudad</th>
                  <th scope="col">Estado</th>
                  <th scope="col">Total</th>
                  <th scope="col">Detalle</th>
                  {canManageOrders && <th scope="col">Acciones</th>}
                </tr>
              </thead>
              <tbody>
                {orders.map((order) => (
                  <FragmentOrderRow
                    key={order.id}
                    order={order}
                    canManageOrders={canManageOrders}
                    expanded={expandedOrderId === order.id}
                    productsById={productById}
                    pendingAction={pendingAction}
                    onToggle={() => setExpandedOrderId((current) => current === order.id ? null : order.id)}
                    onTransition={handleTransition}
                  />
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>

      <footer className="orders-pagination">
        <span>Página {totalPages === 0 ? 0 : page + 1} de {totalPages}</span>
        <div>
          <button type="button" onClick={() => setPage((current) => Math.max(0, current - 1))} disabled={page === 0 || loading}>
            Anterior
          </button>
          <button type="button" onClick={() => setPage((current) => current + 1)} disabled={totalPages === 0 || page + 1 >= totalPages || loading}>
            Siguiente
          </button>
        </div>
      </footer>
    </main>
  );
}

function FragmentOrderRow({ order, canManageOrders, expanded, productsById, pendingAction, onToggle, onTransition }) {
  const actions = canManageOrders ? (() => {
    if (order.status === 'REGISTRADO') return [['prepare', 'Iniciar preparación'], ['cancel', 'Anular']];
    if (order.status === 'PREPARACION') return [['dispatch', 'Marcar despachado'], ['cancel', 'Anular']];
    if (order.status === 'DESPACHADO') return [['deliver', 'Marcar entregado']];
    return [];
  })() : [];

  return (
    <>
      <tr>
        <td>{order.orderReference || '—'}</td>
        <td>{order.customerName || '—'}</td>
        <td>{order.deliveryCity || '—'}</td>
        <td><span className={'orders-status orders-status-' + String(order.status || '').toLowerCase()}>{statusLabels[order.status] || order.status || '—'}</span></td>
        <td>{money.format(Number(order.totalAmount || 0))}</td>
        <td><button className="orders-link-button" type="button" onClick={onToggle}>{expanded ? 'Ocultar' : 'Ver detalle'}</button></td>
        {canManageOrders && (
          <td>
            <div className="orders-row-actions">
              {actions.map(([action, label]) => (
                <button
                  key={action}
                  type="button"
                  className={action === 'cancel' ? 'orders-danger-button' : ''}
                  onClick={() => onTransition(order, action)}
                  disabled={pendingAction === order.id + ':' + action || Boolean(pendingAction)}
                >
                  {pendingAction === order.id + ':' + action ? 'Procesando…' : label}
                </button>
              ))}
              {actions.length === 0 && <span className="orders-muted">Sin acciones</span>}
            </div>
          </td>
        )}
      </tr>
      {expanded && (
        <tr className="orders-detail-row">
          <td colSpan={canManageOrders ? 7 : 6}>
            <div className="orders-detail">
              <div><strong>Dirección:</strong> {order.deliveryAddress}, {order.deliveryCity}, {order.deliveryCountry}</div>
              <div><strong>Teléfono:</strong> {order.customerPhone || '—'}</div>
              <h3>Productos</h3>
              {order.details?.length ? (
                <ul>
                  {order.details.map((detail) => {
                    const product = productsById.get(Number(detail.productId));
                    return (
                      <li key={detail.id}>
                        {(product?.name || 'Producto #' + detail.productId)}
                        {' — ' + detail.quantity + ' × ' + money.format(Number(detail.unitPrice || 0))}
                        {' = ' + money.format(Number(detail.subtotal || 0))}
                      </li>
                    );
                  })}
                </ul>
              ) : <p className="orders-muted">El pedido no tiene detalles disponibles.</p>}
            </div>
          </td>
        </tr>
      )}
    </>
  );
}

export default OrderList;


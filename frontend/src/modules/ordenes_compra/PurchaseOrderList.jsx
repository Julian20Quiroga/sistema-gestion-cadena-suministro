import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../shared/context/auth';
import { cancelPurchaseOrder, createPurchaseOrder, fetchFreeLocations, fetchProcurementProducts, fetchProcurementSuppliers, fetchProcurementWarehouses, fetchPurchaseOrders, receivePurchaseOrder } from './procurementService';
import './PurchaseOrderList.css';

const rows = (v) => Array.isArray(v) ? v : v?.content || [];
const message = (e) => e.response?.data?.detail || e.response?.data?.message || 'Ocurrió un error al procesar la orden.';
const newLine = () => ({ productId: '', quantity: '1', unitPrice: '' });

export default function PurchaseOrderList() {
  const { token, role } = useAuth();
  const navigate = useNavigate();
  const president = ['PRESIDENT', 'ROLE_PRESIDENT'].includes(role);
  const [orders, setOrders] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [products, setProducts] = useState([]);
  const [warehouses, setWarehouses] = useState([]);
  const [page, setPage] = useState(0);
  const [pages, setPages] = useState(0);
  const [status, setStatus] = useState('');
  const [reload, setReload] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [show, setShow] = useState(false);
  const [form, setForm] = useState({ supplierId: '', warehouseId: '', items: [newLine()] });
  const [notice, setNotice] = useState('');
  const [receiving, setReceiving] = useState(null);
  const [locations, setLocations] = useState([]);
  const [receiveLines, setReceiveLines] = useState({});
  const [working, setWorking] = useState(false);
  const filteredProducts = products.filter((p) => String(p.supplierId) === String(form.supplierId));

  useEffect(() => {
    let live = true;
    Promise.all([fetchProcurementSuppliers({ token }), fetchProcurementProducts({ token }), fetchProcurementWarehouses({ token })])
      .then(([s, p, w]) => { if (live) { setSuppliers(rows(s)); setProducts(rows(p)); setWarehouses(rows(w)); } })
      .catch((e) => { if (live) setError(message(e)); });
    return () => { live = false; };
  }, [token]);

  useEffect(() => {
    let live = true;
    fetchPurchaseOrders({ token, page, size: 10, status })
      .then((data) => { if (live) { setOrders(rows(data)); setPages(data?.totalPages || 0); setError(''); } })
      .catch((e) => { if (live) setError(message(e)); })
      .finally(() => { if (live) setLoading(false); });
    return () => { live = false; };
  }, [token, page, status, reload]);

  const refresh = () => { setLoading(true); setReload((n) => n + 1); };
  const changeLine = (i, field, value) => setForm((f) => ({ ...f, items: f.items.map((line, j) => j === i ? { ...line, [field]: value } : line) }));

  async function save(event) {
    event.preventDefault(); setNotice('');
    if (!form.supplierId || !form.warehouseId || form.items.some((i) => !i.productId || Number(i.quantity) < 1 || i.unitPrice === '' || Number(i.unitPrice) < 0)) {
      setNotice('Completa proveedor, bodega, producto, cantidad y precio válido.'); return;
    }
    setWorking(true);
    try { await createPurchaseOrder({ token, ...form }); setShow(false); setNotice('Orden de compra creada.'); refresh(); }
    catch (e) { setNotice(message(e)); } finally { setWorking(false); }
  }

  async function openReception(order) {
    setReceiving(order); setNotice(''); setWorking(true);
    const initial = {};
    (order.details || []).forEach((d) => { const left = d.quantity - (d.receivedQuantity || 0); if (left > 0) initial[d.productId] = { quantity: String(left), storageSpaceId: '' }; });
    setReceiveLines(initial);
    try { setLocations(rows(await fetchFreeLocations({ token, warehouseId: order.warehouseId }))); }
    catch (e) { setNotice(message(e)); setLocations([]); } finally { setWorking(false); }
  }

  async function receive(event) {
    event.preventDefault();
    const items = Object.entries(receiveLines).filter(([, v]) => Number(v.quantity) > 0).map(([productId, v]) => ({ productId, ...v }));
    if (!items.length || items.some((v) => !v.storageSpaceId)) { setNotice('Indica cantidad y ubicación para los productos recibidos.'); return; }
    setWorking(true);
    try { await receivePurchaseOrder({ token, orderId: receiving.id, items }); setReceiving(null); setNotice('Recepción registrada; inventario actualizado.'); refresh(); }
    catch (e) { setNotice(message(e)); } finally { setWorking(false); }
  }

  async function cancel(order) {
    if (!window.confirm('¿Cancelar esta orden de compra?')) return;
    setWorking(true);
    try { await cancelPurchaseOrder({ token, orderId: order.id }); setNotice('Orden cancelada.'); refresh(); }
    catch (e) { setNotice(message(e)); } finally { setWorking(false); }
  }

  return <main className="procurement-page">
    <header className="procurement-header"><div><button className="procurement-link" onClick={() => navigate('/dashboard')}>← Volver al dashboard</button><h1>Abastecimiento</h1><p>Consulta y gestiona las órdenes de compra a proveedores.</p></div><div><button onClick={refresh}>Actualizar</button>{president && <button className="procurement-primary" onClick={() => { setForm({ supplierId: '', warehouseId: '', items: [newLine()] }); setShow(true); setNotice(''); }}>Nueva orden</button>}</div></header>
    {notice && <div className={notice === "Orden de compra creada." || notice === "Recepción registrada; inventario actualizado." || notice === "Orden cancelada." ? "procurement-notice success" : "procurement-notice error"}>{notice}</div>}
    {show && <section className="procurement-card"><h2>Nueva orden de compra</h2><form className="procurement-form" onSubmit={save}>
      <label>Proveedor<select value={form.supplierId} required onChange={(e) => setForm({ ...form, supplierId: e.target.value, items: [newLine()] })}><option value="">Selecciona proveedor</option>{suppliers.map((s) => <option key={s.id} value={s.id}>{s.name} · {s.nit}</option>)}</select></label>
      <label>Bodega<select value={form.warehouseId} required onChange={(e) => setForm({ ...form, warehouseId: e.target.value })}><option value="">Selecciona bodega</option>{warehouses.map((w) => <option key={w.id} value={w.id}>{w.name}</option>)}</select></label>
      <div className="procurement-lines"><h3>Productos</h3>{form.items.map((line, i) => <div className="procurement-line" key={i}>
        <label>Producto<select required value={line.productId} onChange={(e) => changeLine(i, 'productId', e.target.value)}><option value="">Selecciona producto</option>{filteredProducts.map((p) => <option key={p.id} value={p.id}>{p.name} · {p.sku}</option>)}</select></label>
        <label>Cantidad<input type="number" min="1" value={line.quantity} onChange={(e) => changeLine(i, 'quantity', e.target.value)} /></label>
        <label>Precio unitario<input type="number" min="0" step="0.01" required value={line.unitPrice} onChange={(e) => changeLine(i, 'unitPrice', e.target.value)} /></label>
        <button type="button" onClick={() => setForm({ ...form, items: form.items.filter((_, j) => j !== i) })} disabled={form.items.length < 2}>Quitar</button>
      </div>)}<button type="button" onClick={() => setForm({ ...form, items: [...form.items, newLine()] })}>Agregar producto</button></div>
      <button className="procurement-primary" disabled={working}>{working ? 'Guardando…' : 'Crear orden'}</button><button type="button" onClick={() => setShow(false)}>Cerrar</button>
    </form></section>}
    <section className="procurement-card"><label>Filtrar estado<select value={status} onChange={(e) => { setLoading(true); setStatus(e.target.value); setPage(0); }}><option value="">Todos</option>{['REGISTRADA','RECIBIDA_PARCIAL','RECIBIDA','ANULADA'].map((v) => <option key={v}>{v}</option>)}</select></label>
      <h2>Órdenes de compra</h2>{error ? <p className="procurement-error">{error}</p> : loading ? <p>Cargando…</p> : !orders.length ? <p>No hay órdenes para este filtro.</p> :
      <div className="procurement-scroll"><table><thead><tr><th>Orden</th><th>Proveedor</th><th>Bodega</th><th>Estado</th><th>Productos recibidos / pedidos</th><th>Acciones</th></tr></thead><tbody>{orders.map((o) => {
        const actionable = ['REGISTRADA','RECIBIDA_PARCIAL'].includes(o.status);
        return <tr key={o.id}><td>#{o.id}</td><td>{suppliers.find((s) => s.id === o.supplierId)?.name || ('#' + o.supplierId)}</td><td>{warehouses.find((w) => w.id === o.warehouseId)?.name || ('#' + o.warehouseId)}</td><td>{o.status}</td><td>{(o.details || []).map((d) => <div key={d.id || d.productId}>{products.find((p) => p.id === d.productId)?.name || ('Producto #' + d.productId)}: {d.receivedQuantity || 0}/{d.quantity}</div>)}</td><td>{actionable && <><button onClick={() => openReception(o)}>Registrar recepción</button>{president && <button onClick={() => cancel(o)}>Cancelar</button>}</>}</td></tr>;
      })}</tbody></table></div>}
    </section>
    <footer className="procurement-pagination"><span>Página {pages ? page + 1 : 0} de {pages}</span><button disabled={!page || loading} onClick={() => { setLoading(true); setPage(page - 1); }}>Anterior</button><button disabled={page + 1 >= pages || loading} onClick={() => { setLoading(true); setPage(page + 1); }}>Siguiente</button></footer>
    {receiving && <div className="procurement-overlay"><section className="procurement-card"><h2>Recepción de orden #{receiving.id}</h2>{!locations.length ? <p>No hay ubicaciones disponibles en esta bodega.</p> : <form onSubmit={receive}>{(receiving.details || []).filter((d) => d.quantity > (d.receivedQuantity || 0)).map((d) => <div className="procurement-line" key={d.productId}><strong>{products.find((p) => p.id === d.productId)?.name || ('Producto #' + d.productId)}</strong><label>Cantidad<input type="number" min="0" max={d.quantity - (d.receivedQuantity || 0)} value={receiveLines[d.productId]?.quantity || '0'} onChange={(e) => setReceiveLines({ ...receiveLines, [d.productId]: { ...receiveLines[d.productId], quantity: e.target.value } })} /></label><label>Ubicación<select value={receiveLines[d.productId]?.storageSpaceId || ''} onChange={(e) => setReceiveLines({ ...receiveLines, [d.productId]: { ...receiveLines[d.productId], storageSpaceId: e.target.value } })}><option value="">Selecciona ubicación</option>{locations.map((l) => <option key={l.id} value={l.id}>{l.name} · libre {l.freeCapacity}</option>)}</select></label></div>)}<button className="procurement-primary" disabled={working}>Confirmar recepción</button></form>}<button onClick={() => setReceiving(null)}>Cerrar</button></section></div>}
  </main>;
}




import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../shared/context/AuthContext';
import { fetchDispatches, transitionDispatch } from './dispatchService';
import './DispatchList.css';

const labels = { PENDIENTE: 'Pendiente', PREPARANDO: 'En preparación', EN_TRANSPORTE: 'En transporte', ENTREGADO: 'Entregado', CANCELADO: 'Cancelado' };
const getError = (e) => e.response?.data?.detail || e.response?.data?.message || 'No fue posible completar la operación.';

export default function DispatchList() {
  const { token } = useAuth();
  const navigate = useNavigate();
  const [rows, setRows] = useState([]);
  const [status, setStatus] = useState('');
  const [page, setPage] = useState(0);
  const [pages, setPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [busy, setBusy] = useState(null);
  const [refresh, setRefresh] = useState(0);

  useEffect(() => {
    let active = true;

    fetchDispatches({ token, status, page, size: 10 })
      .then((data) => { if (active) { setError(''); setRows(data.content || []); setPages(data.totalPages || 0); } })
      .catch((e) => { if (active) setError(getError(e)); })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [token, status, page, refresh]);

  async function act(row, action) {
    const prompts = { prepare: '¿Iniciar el alistamiento?', ship: '¿Despachar el pedido? Se descontarán las unidades reservadas del inventario.', deliver: '¿Confirmar la entrega?', cancel: '¿Cancelar este despacho?' };
    if (!window.confirm(prompts[action])) return;
    setBusy(row.id); setError(''); setSuccess('');
    try {
      await transitionDispatch({ token, id: row.id, action });
      setSuccess(action === 'ship' ? 'Despacho enviado; el inventario se descontó.' : 'Estado del despacho actualizado.');
      setRefresh((n) => n + 1);
    } catch (e) { setError(getError(e)); }
    finally { setBusy(null); }
  }

  function actions(row) {
    const disabled = busy === row.id;
    if (row.status === 'PENDIENTE') return <><button disabled={disabled} onClick={() => act(row,'prepare')}>Iniciar alistamiento</button><button className="danger" disabled={disabled} onClick={() => act(row,'cancel')}>Cancelar</button></>;
    if (row.status === 'PREPARANDO') return <><button disabled={disabled} onClick={() => act(row,'ship')}>Despachar</button><button className="danger" disabled={disabled} onClick={() => act(row,'cancel')}>Cancelar</button></>;
    if (row.status === 'EN_TRANSPORTE') return <button disabled={disabled} onClick={() => act(row,'deliver')}>Confirmar entrega</button>;
    return 'Sin acciones';
  }

  return <main className="dispatch-page">
    <header className="dispatch-heading"><div><button className="back" onClick={() => navigate('/dashboard')}>← Volver al dashboard</button><h1>Despachos</h1><p>Consulta y actualiza el estado de los despachos de pedidos.</p></div><button onClick={() => navigate('/pedidos')}>Ir a pedidos</button></header>
    {success && <p className="success">{success}</p>}{error && <p className="error">{error}</p>}
    <section className="dispatch-filters"><label>Estado<select value={status} onChange={(e) => { setStatus(e.target.value); setPage(0); }}><option value="">Todos los estados</option>{Object.entries(labels).map(([key,value]) => <option key={key} value={key}>{value}</option>)}</select></label><button disabled={loading} onClick={() => setRefresh((n) => n + 1)}>Actualizar</button></section>
    <section className="dispatch-table">
      {loading ? <p className="empty">Cargando despachos…</p> : rows.length === 0 ? <p className="empty">No hay despachos para mostrar. Puedes crear uno desde un pedido.</p> :
      <div className="scroll"><table><thead><tr><th>Despacho</th><th>Pedido</th><th>Bodega</th><th>Estado</th><th>Acciones</th></tr></thead><tbody>{rows.map((row) => <tr key={row.id}><td>#{row.id}</td><td>#{row.orderId}</td><td>#{row.warehouseId}</td><td><span className="badge">{labels[row.status] || row.status}</span></td><td><div className="actions">{actions(row)}</div></td></tr>)}</tbody></table></div>}
    </section>
    <footer className="pagination"><span>Página {pages ? page + 1 : 0} de {pages}</span><div><button disabled={loading || page === 0} onClick={() => setPage((n) => n - 1)}>Anterior</button><button disabled={loading || !pages || page + 1 >= pages} onClick={() => setPage((n) => n + 1)}>Siguiente</button></div></footer>
  </main>;
}

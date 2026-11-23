import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../shared/context/auth';
import {
  createSnapshot,
  fetchSnapshots,
  fetchWarehouses,
  optimizeWarehouse,
  restoreSnapshot,
} from './optimizationService';
import './Optimization.css';

function Optimization() {
  const { token } = useAuth();
  const navigate = useNavigate();
  const [warehouses, setWarehouses] = useState([]);
  const [warehouseId, setWarehouseId] = useState('');
  const [snapshots, setSnapshots] = useState([]);
  const [snapshotName, setSnapshotName] = useState('');
  const [autoSnapshot, setAutoSnapshot] = useState(true);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [working, setWorking] = useState(false);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');

  const loadSnapshots = useCallback(async (id) => {
    if (!id) {
      setSnapshots([]);
      return;
    }
    const list = await fetchSnapshots({ token, warehouseId: id });
    setSnapshots(Array.isArray(list) ? list : []);
  }, [token]);

  useEffect(() => {
    let current = true;

    async function loadWarehouses() {
      setLoading(true);
      setError('');
      try {
        const result = await fetchWarehouses({ token });
        const list = result.content || [];
        if (current) {
          setWarehouses(list);
          if (list.length) setWarehouseId(String(list[0].id));
        }
      } catch (requestError) {
        if (current) {
          setError(
            requestError.response?.data?.detail ||
              'No fue posible cargar las bodegas para optimizar.'
          );
        }
      } finally {
        if (current) setLoading(false);
      }
    }

    if (token) loadWarehouses();

    return () => {
      current = false;
    };
  }, [token]);

  useEffect(() => {
    let current = true;

    async function loadSelectedSnapshots() {
      if (!warehouseId || !token) {
        setSnapshots([]);
        return;
      }
      try {
        const list = await fetchSnapshots({ token, warehouseId });
        if (current) setSnapshots(Array.isArray(list) ? list : []);
      } catch (requestError) {
        if (current) {
          setError(
            requestError.response?.data?.detail ||
              'No fue posible cargar los snapshots de la bodega.'
          );
        }
      }
    }

    loadSelectedSnapshots();
    return () => {
      current = false;
    };
  }, [token, warehouseId]);

  function getErrorMessage(requestError, fallback) {
    return requestError.response?.data?.detail ||
      requestError.response?.data?.message ||
      fallback;
  }

  async function handleOptimize() {
    if (!warehouseId) return;
    const confirmed = window.confirm(
      'Se analizará el inventario y se mostrarán propuestas de reubicación. Esta acción no mueve existencias.' +
        (autoSnapshot ? ' Se guardará un snapshot automático del layout.' : '') +
        ' ¿Deseas continuar?'
    );
    if (!confirmed) return;

    setWorking(true);
    setError('');
    setNotice('');
    setResult(null);
    try {
      const response = await optimizeWarehouse({
        token,
        warehouseId,
        autoSnapshot,
      });
      setResult(response);
      setNotice(response.summary || 'La optimización terminó.');
      await loadSnapshots(warehouseId);
    } catch (requestError) {
      setError(getErrorMessage(requestError, 'No fue posible optimizar la bodega.'));
    } finally {
      setWorking(false);
    }
  }

  async function handleCreateSnapshot(event) {
    event.preventDefault();
    if (!warehouseId || !snapshotName.trim()) return;

    setWorking(true);
    setError('');
    setNotice('');
    try {
      const created = await createSnapshot({
        token,
        warehouseId,
        snapshotName: snapshotName.trim(),
      });
      setSnapshotName('');
      setNotice('Snapshot "' + created.snapshotName + '" creado correctamente.');
      await loadSnapshots(warehouseId);
    } catch (requestError) {
      setError(getErrorMessage(requestError, 'No fue posible crear el snapshot.'));
    } finally {
      setWorking(false);
    }
  }

  async function handleRestore(snapshot) {
    const confirmed = window.confirm(
      'Se restaurará la distribución guardada en "' + snapshot.snapshotName + '". ¿Continuar?'
    );
    if (!confirmed) return;

    setWorking(true);
    setError('');
    setNotice('');
    try {
      await restoreSnapshot({
        token,
        warehouseId,
        snapshotId: snapshot.id,
      });
      setNotice('Distribución restaurada desde "' + snapshot.snapshotName + '".');
      setResult(null);
      await loadSnapshots(warehouseId);
    } catch (requestError) {
      setError(getErrorMessage(requestError, 'No fue posible restaurar el snapshot.'));
    } finally {
      setWorking(false);
    }
  }

  const selectedWarehouse = warehouses.find(
    (warehouse) => String(warehouse.id) === String(warehouseId)
  );

  return (
    <main className="optimization-page">
      <header className="optimization-header">
        <div>
          <button
            className="optimization-back-button"
            type="button"
            onClick={() => navigate('/dashboard')}
          >
            ← Volver al dashboard
          </button>
          <h1>Optimización de bodega</h1>
          <p>Analiza reubicaciones y guarda puntos de restauración del layout.</p>
        </div>
      </header>

      {notice && <p className="optimization-notice" role="status">{notice}</p>}
      {error && <p className="optimization-error" role="alert">{error}</p>}

      <section className="optimization-panel">
        <div className="optimization-controls">
          <label>
            Bodega
            <select
              value={warehouseId}
              onChange={(event) => {
                setWarehouseId(event.target.value);
                setResult(null);
                setError('');
              }}
              disabled={loading || warehouses.length === 0}
            >
              {loading && <option value="">Cargando bodegas…</option>}
              {!loading && warehouses.length === 0 && <option value="">No hay bodegas</option>}
              {warehouses.map((warehouse) => (
                <option key={warehouse.id} value={warehouse.id}>
                  {warehouse.name}
                </option>
              ))}
            </select>
          </label>

          <label className="optimization-checkbox">
            <input
              type="checkbox"
              checked={autoSnapshot}
              onChange={(event) => setAutoSnapshot(event.target.checked)}
            />
            Guardar snapshot automático antes de optimizar
          </label>

          <button
            className="optimization-primary-button"
            type="button"
            onClick={handleOptimize}
            disabled={!warehouseId || loading || working}
          >
            {working ? 'Procesando…' : 'Optimizar distribución'}
          </button>
        </div>
        {selectedWarehouse && (
          <p className="optimization-help">
            Bodega seleccionada: {selectedWarehouse.name} · #{selectedWarehouse.id}
          </p>
        )}
      </section>

      {result && (
        <section className="optimization-panel">
          <div className="optimization-result-heading">
            <div>
              <h2>Propuestas de reubicación</h2>
              <p>{result.summary || 'Resultado del análisis de layout.'}</p>
            </div>
            {result.preOptimizationSnapshotId && (
              <span className="optimization-snapshot-badge">
                Snapshot previo #{result.preOptimizationSnapshotId}
              </span>
            )}
          </div>

          {(result.proposals || []).length === 0 ? (
            <p className="optimization-empty">No se requieren reubicaciones para esta bodega.</p>
          ) : (
            <div className="optimization-table-wrap">
              <table className="optimization-table">
                <thead>
                  <tr>
                    <th>Producto / SKU</th>
                    <th>Origen</th>
                    <th>Destino</th>
                    <th>Cantidad</th>
                    <th>Clasificación</th>
                    <th>Motivo</th>
                  </tr>
                </thead>
                <tbody>
                  {result.proposals.map((proposal, index) => (
                    <tr key={proposal.productId + '-' + proposal.fromLocationId + '-' + index}>
                      <td>#{proposal.productId} · {proposal.productSku || '—'}</td>
                      <td>{proposal.fromLocationName || '#' + proposal.fromLocationId}</td>
                      <td>{proposal.toLocationName || '#' + proposal.toLocationId}</td>
                      <td>{proposal.quantity}</td>
                      <td>{proposal.abcClassification || '—'}</td>
                      <td>{proposal.reason || '—'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      )}

      <section className="optimization-panel">
        <div className="optimization-result-heading">
          <div>
            <h2>Snapshots de la bodega</h2>
            <p>Guarda o restaura una distribución anterior.</p>
          </div>
        </div>

        <form className="optimization-snapshot-form" onSubmit={handleCreateSnapshot}>
          <label>
            Nombre del snapshot
            <input
              value={snapshotName}
              onChange={(event) => setSnapshotName(event.target.value)}
              maxLength="100"
              placeholder="Ej. Distribución inicial"
              required
            />
          </label>
          <button
            className="optimization-secondary-button"
            type="submit"
            disabled={!warehouseId || working || !snapshotName.trim()}
          >
            Crear snapshot
          </button>
        </form>

        {snapshots.length === 0 ? (
          <p className="optimization-empty">No hay snapshots guardados para esta bodega.</p>
        ) : (
          <div className="optimization-snapshots">
            {snapshots.map((snapshot) => (
              <article className="optimization-snapshot" key={snapshot.id}>
                <div>
                  <strong>{snapshot.snapshotName}</strong>
                  <span>
                    {snapshot.createdAt
                      ? new Date(snapshot.createdAt).toLocaleString('es-CO')
                      : 'Fecha no disponible'}
                  </span>
                </div>
                <button
                  className="optimization-secondary-button"
                  type="button"
                  onClick={() => handleRestore(snapshot)}
                  disabled={working}
                >
                  Restaurar
                </button>
              </article>
            ))}
          </div>
        )}
      </section>
    </main>
  );
}

export default Optimization;

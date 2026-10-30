import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../shared/context/AuthContext';
import {
  fetchInventoryLevels,
  fetchInventoryMovements,
  fetchInventoryProducts,
  fetchInventoryWarehouses,
} from './inventoryService';
import './Inventory.css';

const pageSize = 20;

function Inventory() {
  const { token } = useAuth();
  const navigate = useNavigate();
  const [filters, setFilters] = useState({ productId: '', warehouseId: '' });
  const [appliedFilters, setAppliedFilters] = useState({ productId: '', warehouseId: '' });
  const [page, setPage] = useState(0);
  const [levels, setLevels] = useState([]);
  const [movements, setMovements] = useState([]);
  const [products, setProducts] = useState([]);
  const [warehouses, setWarehouses] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [refreshKey, setRefreshKey] = useState(0);

  useEffect(() => {
    let current = true;
    async function loadInventory() {
      setLoading(true);
      setError('');
      try {
        const [levelPage, movementPage, productPage, warehousePage] = await Promise.all([
          fetchInventoryLevels({ token, page, size: pageSize, ...appliedFilters }),
          fetchInventoryMovements({ token, page: 0, size: pageSize, productId: appliedFilters.productId }),
          fetchInventoryProducts({ token }),
          fetchInventoryWarehouses({ token }),
        ]);
        if (!current) return;
        setLevels(levelPage.content || []);
        setTotalPages(levelPage.totalPages || 0);
        setMovements(movementPage.content || []);
        setProducts(productPage.content || []);
        setWarehouses((warehousePage.content || []).filter((warehouse) => warehouse.status === 'ACTIVE'));
      } catch (requestError) {
        if (!current) return;
        const detail = requestError.response?.data?.detail || requestError.response?.data?.message;
        setError(detail || 'No fue posible cargar el inventario. Revisa que el backend esté conectado e inténtalo de nuevo.');
      } finally {
        if (current) setLoading(false);
      }
    }
    loadInventory();
    return () => { current = false; };
  }, [token, page, appliedFilters, refreshKey]);

  const productNames = useMemo(
    () => new Map(products.map((product) => [Number(product.id), product.name])),
    [products]
  );
  const warehouseNames = useMemo(
    () => new Map(warehouses.map((warehouse) => [Number(warehouse.id), warehouse.name])),
    [warehouses]
  );
  const visibleAvailable = levels.reduce((total, row) => total + Number(row.availableQuantity || 0), 0);

  function handleSearch(event) {
    event.preventDefault();
    setPage(0);
    setAppliedFilters({ ...filters });
  }

  function productName(id) {
    return productNames.get(Number(id)) || `Producto #${id}`;
  }

  function movementDescription(movement) {
    return (movement.details || []).map((detail) =>
      `${productName(detail.productId)} · espacio #${detail.storageSpaceId} · cantidad ${detail.quantity}`
    ).join(' | ') || 'Sin detalles';
  }

  function formatDate(value) {
    if (!value) return '—';
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? value : date.toLocaleString('es-CO');
  }

  return (
    <main className="inventory-page">
      <header className="inventory-header">
        <div>
          <button className="inventory-back-button" type="button" onClick={() => navigate('/dashboard')}>
            ← Volver al dashboard
          </button>
          <h1>Inventario</h1>
          <p>Consulta las existencias y los movimientos registrados.</p>
        </div>
        <button className="inventory-refresh-button" type="button" onClick={() => setRefreshKey((key) => key + 1)} disabled={loading}>
          Actualizar
        </button>
      </header>

      {error && <p className="inventory-message inventory-error" role="alert">{error}</p>}

      <form className="inventory-filters" onSubmit={handleSearch}>
        <label>
          Producto
          <select value={filters.productId} onChange={(event) => setFilters((current) => ({ ...current, productId: event.target.value }))}>
            <option value="">Todos los productos</option>
            {products.map((product) => <option key={product.id} value={product.id}>{product.name} · {product.sku}</option>)}
          </select>
        </label>
        <label>
          Bodega
          <select value={filters.warehouseId} onChange={(event) => setFilters((current) => ({ ...current, warehouseId: event.target.value }))}>
            <option value="">Todas las bodegas</option>
            {warehouses.map((warehouse) => <option key={warehouse.id} value={warehouse.id}>{warehouse.name}</option>)}
          </select>
        </label>
        <button type="submit">Buscar</button>
      </form>

      <section className="inventory-summary" aria-live="polite">
        <article><span>Registros de existencias en esta página</span><strong>{loading ? '…' : levels.length}</strong></article>
        <article><span>Unidades disponibles en esta página</span><strong>{loading ? '…' : visibleAvailable}</strong></article>
      </section>

      <section className="inventory-card">
        <div className="inventory-card-heading">
          <div><h2>Niveles de inventario</h2><p>Existencias físicas, reservadas y disponibles por espacio.</p></div>
        </div>
        {loading ? <p className="inventory-message">Cargando inventario…</p> : levels.length === 0 ? (
          <p className="inventory-message">No hay existencias registradas para estos filtros.</p>
        ) : (
          <div className="inventory-table-wrap">
            <table>
              <thead><tr><th>Producto</th><th>Bodega</th><th>Espacio</th><th>Físico</th><th>Reservado</th><th>Disponible</th></tr></thead>
              <tbody>{levels.map((row) => (
                <tr key={row.id}>
                  <td>{productName(row.productId)}</td>
                  <td>{warehouseNames.get(Number(row.warehouseId)) || `Bodega #${row.warehouseId}`}</td>
                  <td>#{row.storageSpaceId}</td>
                  <td>{row.physicalQuantity}</td><td>{row.reservedQuantity}</td><td>{row.availableQuantity}</td>
                </tr>
              ))}</tbody>
            </table>
          </div>
        )}
      </section>

      <section className="inventory-card">
        <div className="inventory-card-heading">
          <div><h2>Movimientos recientes</h2><p>Últimos movimientos registrados para el producto seleccionado.</p></div>
        </div>
        {loading ? <p className="inventory-message">Cargando movimientos…</p> : movements.length === 0 ? (
          <p className="inventory-message">No hay movimientos registrados para estos filtros.</p>
        ) : (
          <div className="inventory-table-wrap">
            <table>
              <thead><tr><th>Fecha</th><th>Tipo</th><th>Detalles</th><th>Motivo</th><th>Estado</th></tr></thead>
              <tbody>{movements.map((movement) => (
                <tr key={movement.id}>
                  <td>{formatDate(movement.date)}</td><td>{movement.type}</td><td>{movementDescription(movement)}</td>
                  <td>{movement.reason || movement.reference || '—'}</td><td>{movement.status}</td>
                </tr>
              ))}</tbody>
            </table>
          </div>
        )}
      </section>

      <footer className="inventory-pagination">
        <span>Página {totalPages === 0 ? 0 : page + 1} de {totalPages}</span>
        <div>
          <button type="button" onClick={() => setPage((value) => Math.max(0, value - 1))} disabled={page === 0 || loading}>Anterior</button>
          <button type="button" onClick={() => setPage((value) => value + 1)} disabled={totalPages === 0 || page + 1 >= totalPages || loading}>Siguiente</button>
        </div>
      </footer>
    </main>
  );
}

export default Inventory;

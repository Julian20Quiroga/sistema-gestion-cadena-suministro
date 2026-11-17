import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../shared/context/AuthContext';
import {
  fetchInventoryLevels,
  fetchInventoryMovements,
  fetchInventoryProducts,
  fetchInventoryWarehouses,
  fetchInventoryFreeLocations,
  registerInventoryEntry,
  createInventoryWarehouse,
  createInventoryStorageSpace,
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
  const [entryOpen, setEntryOpen] = useState(false);
  const [entryForm, setEntryForm] = useState({ productId: '', warehouseId: '', storageSpaceId: '', quantity: '1', reference: '' });
  const [freeLocations, setFreeLocations] = useState([]);
  const [locationsLoading, setLocationsLoading] = useState(false);
  const [entrySubmitting, setEntrySubmitting] = useState(false);
  const [entryError, setEntryError] = useState('');
  const [entrySuccess, setEntrySuccess] = useState('');
  const [setupOpen, setSetupOpen] = useState(false);
  const [warehouseForm, setWarehouseForm] = useState({ name: '', address: '', phone: '', headquarterId: '' });
  const [spaceForm, setSpaceForm] = useState({ warehouseId: '', zoneName: 'Zona principal', rackName: 'Rack 1', locationName: 'Ubicación 1', capacity: '100' });
  const [setupSubmitting, setSetupSubmitting] = useState(false);
  const [setupError, setSetupError] = useState('');
  const [setupSuccess, setSetupSuccess] = useState('');

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

  useEffect(() => {
    let current = true;
    async function loadFreeLocations() {
      if (!entryOpen || !entryForm.warehouseId) {
        setFreeLocations([]);
        return;
      }
      setLocationsLoading(true);
      setEntryError('');
      try {
        const locations = await fetchInventoryFreeLocations({ token, warehouseId: entryForm.warehouseId });
        if (current) {
          setFreeLocations(locations || []);
          setEntryForm((form) => ({ ...form, storageSpaceId: '' }));
        }
      } catch (requestError) {
        if (current) {
          const detail = requestError.response?.data?.detail || requestError.response?.data?.message;
          setEntryError(detail || 'No fue posible cargar las ubicaciones disponibles de la bodega.');
          setFreeLocations([]);
        }
      } finally {
        if (current) setLocationsLoading(false);
      }
    }
    loadFreeLocations();
    return () => { current = false; };
  }, [token, entryOpen, entryForm.warehouseId]);

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

  async function handleEntrySubmit(event) {
    event.preventDefault();
    setEntryError('');
    setEntrySuccess('');
    setEntrySubmitting(true);
    try {
      await registerInventoryEntry({ token, ...entryForm });
      setEntrySuccess('Entrada registrada. Las existencias y movimientos se actualizaron.');
      setEntryForm((form) => ({ ...form, quantity: '1', reference: '' }));
      setRefreshKey((key) => key + 1);
    } catch (requestError) {
      const detail = requestError.response?.data?.detail || requestError.response?.data?.message;
      setEntryError(detail || 'No fue posible registrar la entrada. Revisa los datos e inténtalo de nuevo.');
    } finally {
      setEntrySubmitting(false);
    }
  }

  async function handleWarehouseSubmit(event) {
    event.preventDefault();
    setSetupError('');
    setSetupSuccess('');
    setSetupSubmitting(true);
    try {
      const warehouse = await createInventoryWarehouse({ token, ...warehouseForm });
      setSetupSuccess(`Bodega “${warehouse.name}” creada. Ahora crea su zona, rack y ubicación.`);
      setWarehouseForm({ name: '', address: '', phone: '', headquarterId: '' });
      setSpaceForm((form) => ({ ...form, warehouseId: String(warehouse.id) }));
      setRefreshKey((key) => key + 1);
    } catch (requestError) {
      const detail = requestError.response?.data?.detail || requestError.response?.data?.message;
      setSetupError(detail || 'No fue posible crear la bodega. Verifica el ID de sede y los datos ingresados.');
    } finally {
      setSetupSubmitting(false);
    }
  }

  async function handleStorageSetupSubmit(event) {
    event.preventDefault();
    setSetupError('');
    setSetupSuccess('');
    setSetupSubmitting(true);
    try {
      const zone = await createInventoryStorageSpace({ token, warehouseId: spaceForm.warehouseId, name: spaceForm.zoneName, type: 'ZONE' });
      const rack = await createInventoryStorageSpace({ token, warehouseId: spaceForm.warehouseId, name: spaceForm.rackName, type: 'RACK', parentId: zone.id });
      await createInventoryStorageSpace({ token, warehouseId: spaceForm.warehouseId, name: spaceForm.locationName, type: 'LOCATION', parentId: rack.id, capacity: spaceForm.capacity });
      setSetupSuccess('Espacio creado: zona, rack y ubicación. Ya puedes registrar la entrada de inventario.');
      setRefreshKey((key) => key + 1);
    } catch (requestError) {
      const detail = requestError.response?.data?.detail || requestError.response?.data?.message;
      setSetupError(detail || 'No fue posible completar la creación del espacio de almacenamiento.');
    } finally {
      setSetupSubmitting(false);
    }
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
        <div className="inventory-header-actions">
          <button className="inventory-entry-button" type="button" onClick={() => { setSetupOpen((open) => !open); setSetupError(''); setSetupSuccess(''); }}>
            {setupOpen ? 'Cerrar configuración' : 'Configurar bodegas'}
          </button>
          <button className="inventory-entry-button" type="button" onClick={() => { setEntryOpen((open) => !open); setEntryError(''); setEntrySuccess(''); }}>
            {entryOpen ? 'Cerrar entrada' : 'Registrar entrada'}
          </button>
          <button className="inventory-refresh-button" type="button" onClick={() => setRefreshKey((key) => key + 1)} disabled={loading}>
            Actualizar
          </button>
        </div>
      </header>

      {error && <p className="inventory-message inventory-error" role="alert">{error}</p>}

      {setupOpen && <section className="inventory-card inventory-entry-card">
        <div className="inventory-card-heading"><div><h2>Configurar bodega y ubicación</h2><p>Se necesita una bodega y una ubicación con capacidad antes de ingresar existencias.</p></div></div>
        <form className="inventory-entry-form" onSubmit={handleWarehouseSubmit}>
          <h3 className="inventory-form-subheading">Crear bodega</h3>
          <label>Nombre de bodega<input required maxLength="100" value={warehouseForm.name} onChange={(event) => setWarehouseForm((form) => ({ ...form, name: event.target.value }))} /></label>
          <label>Dirección<input required maxLength="255" value={warehouseForm.address} onChange={(event) => setWarehouseForm((form) => ({ ...form, address: event.target.value }))} /></label>
          <label>Teléfono<input required maxLength="20" value={warehouseForm.phone} onChange={(event) => setWarehouseForm((form) => ({ ...form, phone: event.target.value }))} /></label>
          <label>ID de sede<input required type="number" min="1" step="1" value={warehouseForm.headquarterId} onChange={(event) => setWarehouseForm((form) => ({ ...form, headquarterId: event.target.value }))} /></label>
          <button type="submit" disabled={setupSubmitting}>{setupSubmitting ? 'Guardando…' : 'Crear bodega'}</button>
        </form>
        <form className="inventory-entry-form inventory-storage-form" onSubmit={handleStorageSetupSubmit}>
          <h3 className="inventory-form-subheading">Crear ubicación de almacenamiento</h3>
          <label>Bodega<select required value={spaceForm.warehouseId} onChange={(event) => setSpaceForm((form) => ({ ...form, warehouseId: event.target.value }))}>
            <option value="">Selecciona una bodega</option>{warehouses.map((warehouse) => <option key={warehouse.id} value={warehouse.id}>{warehouse.name}</option>)}
          </select></label>
          <label>Zona<input required maxLength="100" value={spaceForm.zoneName} onChange={(event) => setSpaceForm((form) => ({ ...form, zoneName: event.target.value }))} /></label>
          <label>Rack<input required maxLength="100" value={spaceForm.rackName} onChange={(event) => setSpaceForm((form) => ({ ...form, rackName: event.target.value }))} /></label>
          <label>Ubicación<input required maxLength="100" value={spaceForm.locationName} onChange={(event) => setSpaceForm((form) => ({ ...form, locationName: event.target.value }))} /></label>
          <label>Capacidad de la ubicación<input required type="number" min="1" step="1" value={spaceForm.capacity} onChange={(event) => setSpaceForm((form) => ({ ...form, capacity: event.target.value }))} /></label>
          <p className="inventory-form-hint">Se crearán en orden Zona → Rack → Ubicación. La capacidad es el máximo de unidades que puede almacenar esa ubicación.</p>
          <button type="submit" disabled={setupSubmitting || warehouses.length === 0}>{setupSubmitting ? 'Guardando…' : 'Crear espacio'}</button>
        </form>
        {setupError && <p className="inventory-message inventory-error" role="alert">{setupError}</p>}
        {setupSuccess && <p className="inventory-message inventory-success" role="status">{setupSuccess}</p>}
      </section>}

      {entryOpen && <section className="inventory-card inventory-entry-card">
        <div className="inventory-card-heading"><div><h2>Registrar entrada de productos</h2><p>Agrega unidades a una ubicación de almacenamiento disponible.</p></div></div>
        <form className="inventory-entry-form" onSubmit={handleEntrySubmit}>
          <label>Producto
            <select required value={entryForm.productId} onChange={(event) => setEntryForm((form) => ({ ...form, productId: event.target.value }))}>
              <option value="">Selecciona un producto</option>
              {products.map((product) => <option key={product.id} value={product.id}>{product.name} · {product.sku}</option>)}
            </select>
          </label>
          <label>Bodega
            <select required value={entryForm.warehouseId} onChange={(event) => setEntryForm((form) => ({ ...form, warehouseId: event.target.value, storageSpaceId: '' }))}>
              <option value="">Selecciona una bodega</option>
              {warehouses.map((warehouse) => <option key={warehouse.id} value={warehouse.id}>{warehouse.name}</option>)}
            </select>
          </label>
          <label>Ubicación
            <select required value={entryForm.storageSpaceId} onChange={(event) => setEntryForm((form) => ({ ...form, storageSpaceId: event.target.value }))} disabled={!entryForm.warehouseId || locationsLoading || freeLocations.length === 0}>
              <option value="">{locationsLoading ? 'Cargando ubicaciones…' : 'Selecciona una ubicación'}</option>
              {freeLocations.map((location) => <option key={location.id} value={location.id}>{location.name} · espacio #{location.id} · capacidad libre: {location.freeCapacity}</option>)}
            </select>
          </label>
          <label>Cantidad
            <input required type="number" min="1" step="1" value={entryForm.quantity} onChange={(event) => setEntryForm((form) => ({ ...form, quantity: event.target.value }))} />
          </label>
          <label>Referencia (opcional)
            <input maxLength="100" value={entryForm.reference} onChange={(event) => setEntryForm((form) => ({ ...form, reference: event.target.value }))} placeholder="Factura o documento de ingreso" />
          </label>
          {entryError && <p className="inventory-message inventory-error" role="alert">{entryError}</p>}
          {entrySuccess && <p className="inventory-message inventory-success" role="status">{entrySuccess}</p>}
          {entryForm.warehouseId && !locationsLoading && freeLocations.length === 0 && !entryError && <p className="inventory-message">Esta bodega no tiene ubicaciones con capacidad disponible. Pide al administrador que configure un espacio de almacenamiento.</p>}
          <button type="submit" disabled={entrySubmitting || locationsLoading || freeLocations.length === 0}>{entrySubmitting ? 'Guardando…' : 'Registrar entrada'}</button>
        </form>
      </section>}

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
